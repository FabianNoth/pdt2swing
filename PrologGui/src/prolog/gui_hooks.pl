:- module(gui_hooks, [
	% facts
	add_fact/2,
	remove_fact/1,
	update_fact/1,
	% relations
	add_relation/1,
	remove_relation/1,
	% persistence
	persist_data/3
]).

:- use_module(utils, [
	get_unbound_goal/2
]).

:- use_module(id_handling, [
	new_id/2,
	output_id/1
]).

:- multifile add_data_hook/2.
:- multifile remove_data_hook/1.
:- multifile update_data_hook/1.
:- multifile output_data_hook/2.

%% add_fact(Goal, AddedId)
%
% add new main entry
%
add_fact(Goal, AddedId) :-
	add_data_hook(Goal, AddedId),
	!.
	
add_fact(Goal, AddedId) :-	% default
	Goal =.. [Functor, AddedId | _],
	new_id(Functor, AddedId),
	assert(Goal).

%% add_relation(Goal)
%
% add new relation entry
%
add_relation(Goal) :-
	add_data_hook(Goal, _),
	!.
	
add_relation(Goal) :-	% default
	ground(Goal),
	( call(Goal)
	-> true
	;  assert(Goal)).

	
%% remove_fact(Goal)
%
% remove main entry
%
remove_fact(Goal) :-
	remove_data_hook(Goal),
	!.
	
remove_fact(Goal) :-	% default
	get_unbound_goal(Goal, NewGoal),
	retractall(NewGoal).
	
%% remove_relation(Goal)
%
% remove relation entry
%
remove_relation(Goal) :-
	remove_data_hook(Goal),
	!.
	
remove_relation(Goal) :-	% default
	retractall(Goal).
	
%% update_fact(Goal)
%
% update entry
%
update_fact(Goal) :-
	update_data_hook(Goal),
	!.
	
update_fact(Goal) :-	% default
	get_unbound_goal(Goal, NewGoal),
	retractall(NewGoal),
	assert(Goal).

%% persist_data(Functor, Arity, File)
%
% save entries to file
%
persist_data(Functor, Arity, File) :-
	tell(File),
	output_id(Functor),
	output_data(Functor, Arity),
	told.

%% output_data(Functor, Arity)
%
% write all the entries
%
output_data(Functor, Arity) :-
	output_data_hook(Functor, Arity),
	!.
	
output_data(Functor, Arity) :-	% default
	listing(Functor/Arity).

%
%:- multifile assert_hook/1.
%:- multifile check_for_existing_value_hook/2.
%:- multifile derived_fact/3.
%:- dynamic current_id/1.
%:- multifile current_id/1.
%
%assert_as_new(Module:Goal) :-
%	assert_as_new(Module:Goal, _).	
%	
%assert_as_new(Module:Goal, Id) :-
%	new_id(Id),
%	Goal =.. [Functor, Id | _],
%	assert(Module:Goal),
%	assert_derived_facts(Module:Functor, Id).
%	
%assert_derived_facts(Module:Functor, Id) :-
%	derived_fact(a, Module:Functor, ModuleD:Derived),
%	Derived =.. [_, Id | _],
%	assert(ModuleD:Derived),
%	fail.
%	
%assert_derived_facts(_,_).
%	
%new_id(Id) :-
%	current_id(Current),
%	retractall(data_handling:current_id(_)),
%	Id is Current + 1,
%	assert(current_id(Id)).
%
%assert_if_not_exists(Goal) :-
%	assert_hook(Goal),
%	!.
%	
%assert_if_not_exists(Goal) :-
%	ground(Goal),
%	( call(Goal)
%	-> true
%	;  assert(Goal)).
%	
%retract_with_derived(Module:Goal) :-
%	Goal =.. [Functor, Id | _],
%	retractall(Module:Goal),
%	retract_derived_facts(Module:Functor, Id).
%	
%retract_derived_facts(Module:Functor, Id) :-
%	derived_fact(r, Module:Functor, ModuleD:Derived),
%	Derived =.. [_, Id | _],
%	retractall(ModuleD:Derived),
%	fail.
%	
%assert_derived_facts(_,_).
%
%check_for_existing_value(Module:Functor, Value) :-
%	check_for_existing_value_hook(Module:Functor, Value),
%	!.
