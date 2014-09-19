:- object(moviedb_model,
    extends(meta_model)).

%%%%%%%%%
% facts %
%%%%%%%%%

fact_type(actor, [
	arg(id,   id, [main]),
	arg(name, atom, [unique])
]).

fact_type(movie, [
	arg(id,    id, [main]),
	arg(name,  atom, [unique]),
	arg(genre, atom(genre), [])
]).

fact_type(role, [
	arg(id,       id, [main]),
	arg(actor,    ref(actor), []),
	arg(movie,    ref(movie), []),
	arg(rolename, atom, [])
]).

fact_type(tag, [
	arg(id,   id, [main]),
	arg(name, atom, [])
]).

%%%%%%%%%
% table %
%%%%%%%%%
display_type(actor, full, default).
display_type(movie, [id, name], default).
display_type(role,  full, default).
display_type(tag,   full, default).

%%%%%%%%%%%%%%
%% relations %
%%%%%%%%%%%%%%

relation_rating_type(movie_rating, [
	arg(id, ref(movie), [unique]),
	arg(rating, unsure_number(0, 10), []),
	arg(funny, unsure_number(0, 10), []),
	arg(action, unsure_number(0, 10), [])
]).

relation_many_type(tagged_movie, [
	arg(id, ref(movie), []),
	arg(tag, ref(tag), [])
]).

relation_single_type(actor_details, [
	arg(id, ref(actor), []),
	arg(awesomeness, unsure_number(0,10), []),
	arg(birthday, number, [])
]).


%%%%%%%%%%%%%%%%
%% fixed atoms %
%%%%%%%%%%%%%%%%

fixed_atom(genre, ['', 'Action', 'Drama', 'Fantasy', 'Horror']).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        relation dummies         %
% (for single & rating relations) %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

relation_dummy(movie, Id, movie_rating(Id, u(0), u(0), u(0))).
relation_dummy(actor, Id, actor_details(Id, u(0), 0)).

% bundles %
bundle(actor, [actor_details]).
bundle(movie, [movie_rating, tagged_movie]).
bundle(role, []).
bundle(tag, []).

text_file(movie).

:- end_object.