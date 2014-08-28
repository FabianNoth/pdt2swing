%% meta_model

:- object(meta_model).

:- public(element/2).
:- public(fact_type/2).		% to be implemented by concrete metamodel
:- public(relation_type/2).		% to be implemented by concrete metamodel
:- public(fixed_atom/2).	% to be implemented by concrete metamodel
:- public(relation_dummy/3).	% to be implemented by concrete metamodel
:- public(argument_value/4).
:- public(argument_value_term/4).
:- public(get_term/2).
:- public(get_term/3).
:- public(get_term_for_main/3).
:- public(check/0).

:- public(relations/4).

:- private(check_args/1).
:- private(check_relation_dummies/0).


:- discontiguous(fact_type/2).
:- discontiguous(relation_type/2).
:- discontiguous(fixed_atom/2).

%% fact_type(Functor, Args)
%
% to be implementend by the subclass
%
% Example:
% fact_type(my_fact, [
%	(id,	id,		[]),
%	(name,	atom,	[unique, main])
% ]).
%

element(Name, Args) :-
	::fact_type(Name, Args).
	
element(Name, Args) :-
	::relation_type(Name, Args).
	
relations(Type, Id, Relation, Term) :-
	::fact_type(Type, _),
	::relation_type(Relation, Args),
	lists:member((Name,Type,_), Args),
	argument_value_term(Relation, Name, Id, Term).
	
%% check
%
% checks the metamodel
check :-
	forall(	::element(_, Args),
			check_args(Args)),
	( check_relation_dummies
	-> true
	; writeln('ERROR: check_relation_dummies failed'), fail).
	
	
check_relation_dummies :-
	\+(::relation_dummy(_, id, _)).
	
check_relation_dummies :-
	::relation_dummy(Type, id, RelationTerm),
	RelationTerm =.. [RelationFunctor | Params],
	::fact_type(Type, _),
	::relation_type(RelationFunctor, Constraints),
	check_relation_dummy_args(Params, Constraints).
	
check_relation_dummy_args([], []) :- !.
check_relation_dummy_args([Param|Params], [(_, Type, _)|Constraints]) :-
	(Param == id
	-> true
	; db_controller::check_argument_type(add, Type, Param)),
	check_relation_dummy_args(Params, Constraints).
	
			
check_args(Args) :-
	forall(	lists:member(Arg, Args),
			check_arg(Arg)
	).
			
check_arg((_Name, Type, _Keys)) :-
	lists:member(Type, [id, atom, number]), !.
	
check_arg((_Name, atom(Type), _Keys)) :-
	::fixed_atom(Type,_), !.
	
check_arg((_Name, number(From, To), _Keys)) :-
	number(From),
	number(To), !.

check_arg((_Name, Type, _Keys)) :-
	::fact_type(Type, _), !.
	
check_arg((_Name, Type, _Keys)) :-
	format('Error: ~w is no valid Type~n', [Type]).
	

	
%% get_term(Functor, Term)
%
% gets an (empty) Term for the specific functor
get_term(Functor, Term) :-
	::element(Functor, Args),
	length(Args, Le),
	length(Tail, Le),
	Term =.. [Functor | Tail].
	
%% get_term(Functor, Id, Term)
%
% gets a Term where only the Id is bound
get_term(Functor, Id, Term) :-
	::element(Functor, Args),
	length(Args, Le),
	Le2 is Le - 1,
	length(Tail, Le2),
	Term =.. [Functor, Id | Tail].
	
%% get_term_for_main(Functor, Main, Term)
%
% gets a term where the main element is bound
get_term_for_main(Functor, Main, Term) :-
	::element(Functor, Args),
	length(Args, Le),
	length(Tail, Le),
	main_element_index(Args, 1, MainIndex),
	lists:nth1(MainIndex, Tail, Main),
	Term =.. [Functor | Tail].

main_element_index([], _, 1) :- !.
main_element_index([(_, _, Keys)|_], Count, Index) :-
	lists:member(main, Keys),
	!,
	Index = Count.
	
main_element_index([_|Tail], Count, Index) :-
	CountInc is Count + 1,
	main_element_index(Tail, CountInc, Index).
	
	

%% argument_value(Functor, Id, Name, Value)
%
% gets the value of argument with name Name
argument_value(Functor, Id, Name, Value) :-
	::element(Functor, Args),
	lists:nth0(N, Args, (Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(0, TermList, Id),
	lists:nth0(N, TermList, Value),
	atom_concat(Functor,'_store',StoreName),
	call(StoreName::Term).
	
argument_value_term(Functor, Name, Value, Term) :-
	::element(Functor, Args),
	lists:nth0(N, Args, (Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(N, TermList, Value).
	
	
:- end_object.