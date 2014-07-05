:- object(moviedb_model,
    extends(meta_model)).

fact_type(actor, [
	(id, id, []),
	(name, atom, [unique, main])
]).

fact_type(movie, [
	(id, id, []),
	(name, atom, [unique, main]),
	(rating, number, [])
]).

%relation_type(role, [
%	(actor, actor),
%	(movie, movie)
%]).
    
:- end_object.