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

fact_type(role, [
	(id, id, []),
	(actor, actor, []),
	(movie, movie, []),
	(rolename, atom, [])
]).
    
:- end_object.