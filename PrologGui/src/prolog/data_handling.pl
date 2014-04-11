:- module(data_handling, [
	assert_as_new/1,
	assert_as_new/2,
	assert_if_not_exists/1,
	retract_with_derived/1,
	check_for_existing_value/2
]).

:- multifile assert_hook/1.
:- multifile check_for_existing_value_hook/2.
:- multifile derived_fact/3.
:- dynamic current_id/1.
:- multifile current_id/1.

assert_as_new(Module:Goal) :-
	assert_as_new(Module:Goal, _).	
	
assert_as_new(Module:Goal, Id) :-
	new_id(Id),
	Goal =.. [Functor, Id | _],
	assert(Module:Goal),
	assert_derived_facts(Module:Functor, Id).
	
assert_derived_facts(Module:Functor, Id) :-
	derived_fact(a, Module:Functor, ModuleD:Derived),
	Derived =.. [_, Id | _],
	assert(ModuleD:Derived),
	fail.
	
assert_derived_facts(_,_).
	
new_id(Id) :-
	current_id(Current),
	retractall(data_handling:current_id(_)),
	Id is Current + 1,
	assert(current_id(Id)).

assert_if_not_exists(Goal) :-
	assert_hook(Goal),
	!.
	
assert_if_not_exists(Goal) :-
	ground(Goal),
	( call(Goal)
	-> true
	;  assert(Goal)).
	
retract_with_derived(Module:Goal) :-
	Goal =.. [Functor, Id | _],
	retractall(Module:Goal),
	retract_derived_facts(Module:Functor, Id).
	
retract_derived_facts(Module:Functor, Id) :-
	derived_fact(r, Module:Functor, ModuleD:Derived),
	Derived =.. [_, Id | _],
	retractall(ModuleD:Derived),
	fail.
	
assert_derived_facts(_,_).

check_for_existing_value(Module:Functor, Value) :-
	check_for_existing_value_hook(Module:Functor, Value),
	!.
