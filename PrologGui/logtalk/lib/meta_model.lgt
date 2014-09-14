%% meta_model

:- object(meta_model).

:- public(element/2).
:- public(filter/2).
:- public(argument_names/2).
:- public(display_names/2).
:- public(element_simple_arg/2).
:- public(element_display_arg/2).
:- public(auto_completion/2).
:- public(fact_type/2).		% to be implemented by concrete metamodel
:- public(relation_rating_type/2).	% to be implemented by concrete metamodel
:- public(relation_many_type/2).	% to be implemented by concrete metamodel
:- public(relation_single_type/2).	% to be implemented by concrete metamodel
:- public(display_type/3).		% to be implemented by concrete metamodel
:- public(fixed_atom/2).	% to be implemented by concrete metamodel
:- public(relation_dummy/3).	% to be implemented by concrete metamodel
:- public(bundle/2).	% to be implemented by concrete metamodel
:- public(text_file/1).	% to be implemented by concrete metamodel
:- public(argument_value/4).
:- public(argument_value_term/4).
:- public(get_term/2).
:- public(get_term/3).
:- public(get_term_for_main/3).
:- public(check/0).
:- public(relation_type/2).	

:- public(relations/4).

:- private(check_args/1).
:- private(check_relation_dummies/0).


:- discontiguous(fact_type/2).
:- discontiguous(relation_type/2).
:- discontiguous(relation_rating_type/2).
:- discontiguous(relation_many_type/2).
:- discontiguous(relation_single_type/2).
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

filter(Functor, Filter) :-
	::display_type(Functor, _, default),
	!,
	::element(Functor, Args),
	lists:member(arg(Name, Type, _), Args),
	filter_impl(Type, Name, Filter).
	
filter_impl(atom, Name, Filter) :-
	min_max_combo(Min, Max),
	Filter = filter(Name, Min, Max).

filter_impl(atom(AtomType), Name, Filter) :-
	::fixed_atom(AtomType, Values),
	lists:member(Value, Values),
	Filter = filter(Name, Value).
	
min_max_combo('A', 'B').
min_max_combo('B', 'C').
min_max_combo('C', 'D').
min_max_combo('D', 'E').
min_max_combo('E', 'F').
min_max_combo('F', 'G').
min_max_combo('G', 'H').
min_max_combo('H', 'I').
min_max_combo('I', 'J').
min_max_combo('J', 'K').
min_max_combo('K', 'L').
min_max_combo('L', 'M').
min_max_combo('M', 'N').
min_max_combo('N', 'O').
min_max_combo('O', 'P').
min_max_combo('P', 'Q').
min_max_combo('Q', 'R').
min_max_combo('R', 'S').
min_max_combo('S', 'T').
min_max_combo('T', 'U').
min_max_combo('U', 'V').
min_max_combo('V', 'W').
min_max_combo('W', 'X').
min_max_combo('X', 'Y').
min_max_combo('Y', 'Z').
min_max_combo('Z', 'ZZZZZZZZZ').

argument_names(Functor, Names) :-
	::element(Functor, Args),
	findall(Name, lists:member(arg(Name, _, _), Args), Names).
	
display_names(Functor, Names) :-
	::display_type(Functor, full, _),
	!,
	argument_names(Functor, Names).

display_names(Functor, Names) :-
	::display_type(Functor, Names, _).
	
element(Name, Args) :-
	::fact_type(Name, Args).
	
element(Name, Args) :-
	::relation_type(Name, Args).

relation_type(Name, Args) :-
	::relation_rating_type(Name, Args).
	
relation_type(Name, Args) :-
	::relation_many_type(Name, Args).
	
relation_type(Name, Args) :-
	::relation_single_type(Name, Args).
	
	
element_simple_arg(Name, SimpleArg) :-
	::element(Name, Args),
	lists:member(arg(ArgName, ArgType, _), Args),
	SimpleArg = arg(ArgName, ArgType).
	
element_display_arg(Name, DisplayArg) :-
	::display_type(Name, full, _),
	!,
	element_simple_arg(Name, DisplayArg).

element_display_arg(Name, DisplayArg) :-
	::display_type(Name, ArgNames, _),
	::element(Name, Args),
	lists:member(ArgName, ArgNames),
	lists:member(arg(ArgName, ArgType, _), Args),
	DisplayArg = arg(ArgName, ArgType).
	
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
check_relation_dummy_args([Param|Params], [arg(_, Type, _)|Constraints]) :-
	(Param == id
	-> true
	; db_controller::check_argument_type(add, Type, Param)),
	check_relation_dummy_args(Params, Constraints).
	
			
check_args(Args) :-
	forall(	lists:member(Arg, Args),
			check_arg(Arg)
	).
			
check_arg(arg(_Name, Type, _Keys)) :-
	lists:member(Type, [id, atom, number]), !.
	
check_arg(arg(_Name, atom(Type), _Keys)) :-
	::fixed_atom(Type,_), !.
	
check_arg(arg(_Name, number(From, To), _Keys)) :-
	number(From),
	number(To), !.
	
check_arg(arg(_Name, unsure_number(From, To), _Keys)) :-
	number(From),
	number(To), !.

check_arg(arg(_Name, Type, _Keys)) :-
	::fact_type(Type, _), !.
	
check_arg(arg(_Name, Type, _Keys)) :-
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
main_element_index([arg(_, _, Keys)|_], Count, Index) :-
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
	lists:nth0(N, Args, arg(Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(0, TermList, Id),
	lists:nth0(N, TermList, Value),
	atom_concat(Functor,'_store',StoreName),
	call(StoreName::Term).
	
argument_value_term(Functor, Name, Value, Term) :-
	::element(Functor, Args),
	lists:nth0(N, Args, arg(Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(N, TermList, Value).
	

auto_completion(Functor, Results) :-
	% default for auto_completion: use element with name: "name"
	::element(Functor, _),
	findall(Result, auto_completion_impl(Functor, Result), ResultList),
	lists:sort(ResultList, Results).
	
auto_completion_impl(Functor, Result) :-
%	::element(Functor, Args),
	argument_value(Functor, _Id, name, Result),
	Result \== ''.
	
:- end_object.