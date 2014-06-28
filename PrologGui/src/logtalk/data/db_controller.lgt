:- object(db_controller).

:- public(add/2).
:- public(delete/2).
:- public(update/2).

add(Term, Result) :-
	Term =.. [F, Id | _],
	id_handling::get_id(F, Id),
	db_store::assert(Term),
	Result = success(Id).
	
delete(Term, Result) :-
	!.
	
update(Term, Result) :-
	!.

:- end_object.