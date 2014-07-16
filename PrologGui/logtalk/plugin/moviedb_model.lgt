:- object(moviedb_model,
    extends(meta_model)).

%%%%%%%%%
% facts %
%%%%%%%%%

fact_type(actor, [
	(id, id, []),
	(name, atom, [unique, main])
]).

fact_type(movie, [
	(id, id, []),
	(name, atom, [unique, main]),
	(genre, atom(genre), [])
]).

fact_type(role, [
	(id, id, []),
	(actor, actor, []),
	(movie, movie, []),
	(rolename, atom, [])
]).

fact_type(tag, [
	(id, id, []),
	(name, atom, [])
]).

%%%%%%%%%%%%%
% relations %
%%%%%%%%%%%%%

relation_type(movie_rating, [
	(movie, movie, [unique]),
	(rating, number(0, 10), []),
	(votes, number, [])
]).

relation_type(tagged_movie, [
	(movie, movie, []),
	(tag, tag, [])
]).


%%%%%%%%%%%%%%%
% fixed atoms %
%%%%%%%%%%%%%%%

fixed_atom(genre, ['', 'Action', 'Drama', 'Fantasy', 'Horror']).

%%%%%%%%%%%%%%%%%%%%
% relation dummies %
%%%%%%%%%%%%%%%%%%%%

relation_dummy(movie, Id, movie_rating(Id, 0, 0)).

:- end_object.