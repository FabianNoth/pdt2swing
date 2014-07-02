:- object(role_store,
    extends(db_store)).
    
	%% role(Id, Actor, Film, Rolename)	
	:- public(role/4).
	:- dynamic(role/4).
	:- include('data/role.pl').
	
	output_filename('role.pl').
	get_term(role/4).
	
	
add(role(Id,Actor,Movie,Rolename), Result) :-
	::get_id(Id),
	( actor_store::actor(ActorId, Actor)
	->	( movie_store::movie(MovieId, Movie)
		  ->	( ::assert(role(Id, ActorId, MovieId, Rolename)),
				  Result = success(Id))
		  ; Result = error('no movie with this name')
		)
	; Result = error('no actor with this name')
	).
	
:- end_object.