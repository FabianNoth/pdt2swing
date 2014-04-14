:- module(id_handling, [
	new_id/2,
	output_id/1
]).

:- dynamic(current_id/2).

%% new_id(Functor, Id)
%
new_id(Functor, Id) :-
	( current_id(Functor, Current)
	-> true
	;  Current = 0),
	retractall(current_id(Functor, _)),
	Id is Current + 1,
	assert(current_id(Functor, Id)).
	
output_id(Functor) :-
	( current_id(Functor, Current)
	-> output_id(Functor, Current)
	;  true).
	
output_id(Functor, Id) :-
	writeln(':- dynamic id_handling:current_id/2.'),
	format('~nid_handling:current_id(~w,~w).~n~n', [Functor, Id]).
