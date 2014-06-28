:- object(db_store).

%% movie(Id, Name)
	:- public(movie/2).
	:- dynamic(movie/2).
	:- include('movie.pl').

%% actor(Id, Name)	
	:- public(actor/2).
	:- dynamic(actor/2).
	:- include('actor.pl').
	
	
:- end_object.