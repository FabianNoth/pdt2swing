:- object(moviedb_model,
    extends(meta_model)).

%%%%%%%%%
% facts %
%%%%%%%%%

fact_type(actor, [
	(id, id, [main]),
	(name, atom, [unique])
]).

fact_type(movie, [
	(id, id, [main]),
	(name, atom, [unique]),
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

%%%%%%%%%%%%%%
%% relations %
%%%%%%%%%%%%%%

relation_type(movie_rating, [
	(movie, movie, [unique]),
	(rating, unsure_number(0, 10), []),
	(funny, unsure_number(0, 10), []),
	(action, unsure_number(0, 10), [])
]).

relation_type(tagged_movie, [
	(movie, movie, []),
	(tag, tag, [])
]).


%%%%%%%%%%%%%%%%
%% fixed atoms %
%%%%%%%%%%%%%%%%

fixed_atom(genre, ['', 'Action', 'Drama', 'Fantasy', 'Horror']).

%%%%%%%%%%%%%%%%%%%%%
%% relation dummies %
%%%%%%%%%%%%%%%%%%%%%

relation_dummy(movie, Id, movie_rating(Id, 0, 0)).

% bundles %
bundle(actor, []).
bundle(movie, [movie_rating, tagged_movie]).
bundle(role, []).
bundle(tag, []).

text_file(movie).

:- end_object.