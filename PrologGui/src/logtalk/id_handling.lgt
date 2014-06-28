:- object(id_handling).

:- public(get_id/2).
:- public(output/0).
:- private(current_id/2).
:- dynamic(current_id/2).

	get_id(Functor, Id) :-
		% if there is no current_id, set current to 0
		( current_id(Functor, Current)
		-> true
		; Current = 0),  
		% delete old entry
		retractall(current_id(Functor, _)),
		% add entry with increased id
		Id is Current + 1,
		assert(current_id(Functor, Id)).
		
output :-
	writeln('id_handling: here i am').
	
current_id(movie,3).
current_id(actor,3).
		
:- end_object.