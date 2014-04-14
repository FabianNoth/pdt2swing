:- module(utils, [
	get_unbound_goal/2
]).

	
%% get_unbound_goal(Goal, NewGoal)
%
get_unbound_goal(Goal, NewGoal) :-
	Goal =.. [_, Id | _],
	functor(Goal, F, Arity),
	functor(NewGoal, F, Arity),
	NewGoal =.. [_, Id | _].
