:- object(db_controller).

:- public(add/2).
:- public(delete/2).
:- public(update/2).

:- private(current_model/1).
:- dynamic(current_model/1).

:- public(init_model/1).

:- private(unbind_term/2).

init_model(Metamodel) :-
	once(Metamodel::fact_type(_,_)),
	retractall(current_model(_)),
	assert(current_model(Metamodel)),
	Metamodel::check.

add(Term, Result) :-
	check_arguments(add, Term, Result),
	% cut if argument checking went wrong 
	Result \== success,
	!.
	
add(Term, Result) :-
	% if checking was okay, assert the fact
	get_store_for_term(Term, Store),
	Term =.. [_, Id | _],
	Store::get_id(Id),
	Store::assert(Term),
	Result = success(Id).

delete(Term, Result) :-
	unbind_term(Term, UnboundTerm),
	check_arguments(delete, UnboundTerm, Result),
	% cut if argument checking went wrong 
	Result \== success,
	!.

delete(Term, Result) :-	
	get_store_for_term(Term, Store),
	unbind_term(Term, UnboundTerm),
	(	Store::UnboundTerm
	->	(Store::retractall(UnboundTerm),
		Result=success)
	;	Result=warning('element didn\'t even exist')
	).
	
update(Term, Result) :-
	Term =.. [_, Id | _],
	check_arguments(update(Id), Term, Result),
	% cut if argument checking went wrong 
	Result \== success,
	!.
	
update(Term, Result) :-
	% unbind term for deleting it
	unbind_term(Term, UnboundTerm),
	get_store_for_term(Term, Store),
	% check for existance
	\+(Store::UnboundTerm),
	Result=error('element doesn\'t exist'),
	!.
	
update(Term, Result) :-
	% arguments are correct, and element exists
	unbind_term(Term, UnboundTerm),
	get_store_for_term(Term, Store),
	Store::retractall(UnboundTerm),
	Store::assert(Term),
	Result=success.
	
check_arguments(Context, Term, Result) :-
	functor(Term, Functor, Arity),
	current_model(Model),
	% check if Term is in current metamodel
	Model::fact_type(Functor, ArgTypes),
	length(ArgTypes, Arity),
	
	Term =.. [Functor | Args],
	check_arguments_impl(Functor, Context, ArgTypes, Args, Result).
	
check_arguments_impl(_, _, [], [], 'success') :- !.

check_arguments_impl(Functor, Context, [ArgType|ArgTypes], [Arg|Args], Result) :-
%	ArgType = (Name, Type, Keywords),
	check_argument(Functor, Context, ArgType, Arg, DummyResult),
	(	DummyResult == 'success'
	->	check_arguments_impl(Functor, Context, ArgTypes, Args, Result)
	;	Result = DummyResult
	).
	
check_argument(Functor, Context, (_, Type, _), Arg, Result) :-
	\+(check_argument_type(Context, Type, Arg)),
	atomic_list_concat([Arg, ' is not of type: ', Type], ErrorMsg),
	Result = error(Functor, ErrorMsg),
	!.
	
check_argument(Functor, Context, (Name, _, Keywords), Arg, Result) :-
	lists:member(Key, Keywords),
	current_model(Model),
	\+(check_argument_keyword(Model, Functor, Context, Key, Name, Arg)),
	keyword_error_msg(Functor, Key, Result),
	!.
%	Result = error('Violated constraint: ', Key),

check_argument(_, _, _, _, 'success') :- !.	
	
keyword_error_msg(Functor, Key, error(Functor, 'Violated constraint: ', Key)).
	
	
	
check_argument_type(add, id, Arg)	:- !, var(Arg).
check_argument_type(_, id, Arg)		:- !, integer(Arg).
check_argument_type(delete, _, _)	:- !.
check_argument_type(_, number, Arg)	:- !, number(Arg).
check_argument_type(_, atom, Arg) 	:- !, atom(Arg).

check_argument_type(_, Type, Arg) 	:-
	!,
	current_model(Model),
	Model::get_term(Type, Arg, Term),
	atom_concat(Type,'_store',StoreName),
	StoreName::Term.


% main is just a flag, no checking required
check_argument_keyword(_, _, _, main, _, _).
	
% no checking required for deleting
check_argument_keyword(_, _, delete, unique, _, _).

% added fact: unique value must not exist
check_argument_keyword(Model, Functor, add, unique, Name, Arg) :-
	% check if value is unique 
	\+(Model::argument_value(Functor, _, Name, Arg)),
	!.
	
% updated fact: unique value may only exist in the current fact
check_argument_keyword(Model, Functor, update(_), unique, Name, Arg) :-
	% true if value doesn't exist at all (renaming)
	\+(Model::argument_value(Functor, _, Name, Arg)),
	!.
	
check_argument_keyword(Model, Functor, update(Id), unique, Name, Arg) :-
	% or if Id is the current Id
	Model::argument_value(Functor, Id, Name, Arg),
	!.
	
	
get_store_for_term(Term, Store) :-
	functor(Term, Functor, _),
	% create store name
	atom_concat(Functor, '_store', Store).
	
unbind_term(Term, UnboundTerm) :-
	% extract functor and id from term
	Term =.. [Functor, Id | List],
	% get length of tail
	length(List, Le),
	% create empty tail
	length(EmptyTail, Le),
	% create new Term
	UnboundTerm =.. [Functor, Id | EmptyTail].

	
:- end_object.