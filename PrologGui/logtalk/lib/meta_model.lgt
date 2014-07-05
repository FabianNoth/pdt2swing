:- object(meta_model).

:- public(fact_type/2).

:- public(argument_value/4).
:- public(get_term/2).
:- public(check/0).
:- private(check_args/1).

check :-
	forall(	::fact_type(_, Args),
			check_args(Args)).
			
check_args(Args) :-
	forall(	lists:member(Arg, Args),
			Arg = (_, _, _)).
			%% TODO: improve checking (correct keywords, types ...)
	
	
get_term(Functor, Term) :-
	::fact_type(Functor, Args),
	length(Args, Le),
	length(Tail, Le),
	Term =.. [Functor | Tail].
	
argument_value(Functor, Id, Name, Arg) :-
	::fact_type(Functor, Args),
	lists:nth0(N, Args, (Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(0, TermList, Id),
	lists:nth0(N, TermList, Arg),
	atom_concat(Functor,'_store',StoreName),
	call(StoreName::Term).
	
	
:- end_object.