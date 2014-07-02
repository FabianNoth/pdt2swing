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
	assert(current_model(Metamodel)).

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
	check_arguments(update, Term, Result),
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
	check_arguments_impl(Context, ArgTypes, Args, Result).
	
check_arguments_impl(_, [], [], 'success') :- !.

check_arguments_impl(Context, [(_,Type)|ArgTypes], [Arg|Args], Result) :-
	(	check_type(Context, Type, Arg)
	->	check_arguments_impl(Context, ArgTypes, Args, Result)
	;	Result = error(Arg, 'is not', Type)
	).
	
check_type(add, id, Arg)	:- !, var(Arg).
check_type(_, id, Arg)		:- !, integer(Arg).
check_type(delete, _, _)	:- !.
check_type(_, number, Arg)	:- !, number(Arg).
check_type(_, atom, Arg) 	:- !, atom(Arg).

	
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