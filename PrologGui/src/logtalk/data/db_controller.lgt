:- object(db_controller).

:- public(add/2).
:- public(delete/2).
:- public(update/2).

:- public(get_store/1).

add(Term, Result) :-
	get_store(Store),
	Term =.. [_, Id | _],
	Store::get_id(Id),
	Store::assert(Term),
	Result = success(Id).
	
delete(Term, Result) :-
	get_store(Store),
	(  Store::Term
	-> (Store::retractall(Term),
	    Result=success)
	;  Result=warning('element didn\'t even exist')
	).
	
update(Term, Result) :-
	!.

get_store(actor_store).	

:- end_object.