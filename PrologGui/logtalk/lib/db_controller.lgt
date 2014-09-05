:- object(db_controller).

:- public(show/2).

:- public(add/2).
:- public(delete/2).
:- public(update/2).

:- public(persist/0).

:- public(current_model/1).
:- dynamic(current_model/1).

:- public(init_model/1).

% this needs to be public for checking of dummy relations in metamodel
:- public(check_argument_type/3).

:- private(unbind_term/2).
:- discontiguous(add/2).

init_model(Metamodel) :-
	once(Metamodel::fact_type(_,_)),
	retractall(current_model(_)),
	assert(current_model(Metamodel)),
	Metamodel::check.

show(Functor, Term) :-
	nonvar(Functor),
	current_model(Model),
	Model::get_term(Functor, Term),
	get_store_for_term(Term, Store),
	Store::Term.

show(Functor, Term) :-
	var(Functor),
	Term =.. [Functor|_],
	show(Functor, Term).	
	
persist :-
	current_model(Model),
	Model::element(Name, _),
	get_store_for_functor(Name, Store),
	Store::persist,
	fail.
	
persist.
	
add(Term, Result) :-
	check_arguments(add, Term, Result),
	% cut if argument checking went wrong 
	Result \== success,
	!.
	
add(Term, Result) :-
	% if checking was okay, assert the fact
	get_store_for_term(Term, Store),
	Term =.. [Functor, Id |_],
	current_model(Model),
	Model::fact_type(Functor,_),
	!,
	% it's a fact type, so we need an id
	Store::get_id(Id),
	Store::add(Term),
	add_dummy_relations(Model, Functor, Id),
	Result = success(Id).
	
add_dummy_relations(Model, Functor, Id) :-
	Model::relation_dummy(Functor, Id, RelationTerm),
	get_store_for_term(RelationTerm, Store),
	Store::add(RelationTerm),
	fail.
	
add_dummy_relations(_, _, _).
	
add(Term, Result) :-
	% it's not a fact type, so we need no id
	get_store_for_term(Term, Store),
	Store::add(Term),
	Result = success.
	
	

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
	->	(Store::delete(UnboundTerm),
		Result=success)
	;	Result=warning('element didn\'t even exist')
	),
	current_model(Model),
	UnboundTerm =.. [Functor, Id|_],
	forall( Model::relations(Functor, Id, _, RelationTerm),
			delete_relation(RelationTerm)).
			
delete_relation(Term) :-
	get_store_for_term(Term, Store),
	Store::delete(Term).
	
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
	Store::delete(UnboundTerm),
	Store::add(Term),
	Result=success.
	
check_arguments(Context, Term, Result) :-
	(Context == add
	; Context == update),
	get_store_for_term(Term, Store),
	Store::Term,
	Result = error('Fact already exists'),
	!.
	
check_arguments(Context, Term, Result) :-
	functor(Term, Functor, Arity),
	current_model(Model),
	% check if Term is in current metamodel
	Model::element(Functor, ArgTypes),
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
	term_to_atom(Type, TypeAtom),
	term_to_atom(Arg, ArgAtom),
	atomic_list_concat([ArgAtom, ' is not of type: ', TypeAtom], ErrorMsg),
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
check_argument_type(_, boolean, Arg) 	:- !, (Arg == true ; Arg == false).

check_argument_type(_, number(From, To), Arg) :-
	!,
	number(Arg),
	Arg >= From,
	Arg =< To.

check_argument_type(_, unsure_number(From, To), Arg) :-
	!,
	number(Arg),
	Arg >= From,
	Arg =< To.
	
check_argument_type(_, atom(Type), Arg) :-
	!,
	atom(Arg),
	current_model(Model),
	Model::fixed_atom(Type, FixedAtoms),
	lists:member(Arg, FixedAtoms).

check_argument_type(_, Type, Arg) 	:-
	!,
	current_model(Model),
	Model::get_term_for_main(Type, Arg, Term),
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
	get_store_for_functor(Functor, Store).
	
get_store_for_functor(Functor, Store) :-
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