%% meta_model

:- object(meta_model).

:- public(fact_type/2).	% to be implemented by concrete metamodel
:- public(argument_value/4).
:- public(get_term/2).
:- public(get_term/3).
:- public(check/0).

:- private(check_args/1).


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

%% check
%
% checks the metamodel
check :-
	forall(	::fact_type(_, Args),
			check_args(Args)).
			
check_args(Args) :-
	forall(	lists:member(Arg, Args),
			check_arg(Arg)
	).
			
check_arg((_Name, Type, _Keys)) :-
	lists:member(Type, [id, atom, number]), !.
	
check_arg((_Name, Type, _Keys)) :-
	::fact_type(Type, _), !.
	
check_arg((_Name, Type, _Keys)) :-
	format('Error: ~w is no valid Type~n', [Type]).
	

	
%% get_term(Functor, Term)
%
% gets an (empty) Term for the specific functor
get_term(Functor, Term) :-
	::fact_type(Functor, Args),
	length(Args, Le),
	length(Tail, Le),
	Term =.. [Functor | Tail].
	
%% get_term(Functor, Id, Term)
%
% gets a Term where only the Id is bound
get_term(Functor, Id, Term) :-
	::fact_type(Functor, Args),
	length(Args, Le),
	Le2 is Le - 1,
	length(Tail, Le2),
	Term =.. [Functor, Id | Tail].
	
%% argument_value(Functor, Id, Name, Value)
%
% gets the value of argument with name Name
argument_value(Functor, Id, Name, Value) :-
	::fact_type(Functor, Args),
	lists:nth0(N, Args, (Name,_,_)),
	::get_term(Functor, Term),
	Term =.. [Functor | TermList],
	lists:nth0(0, TermList, Id),
	lists:nth0(N, TermList, Value),
	atom_concat(Functor,'_store',StoreName),
	call(StoreName::Term).
	
	
:- end_object.