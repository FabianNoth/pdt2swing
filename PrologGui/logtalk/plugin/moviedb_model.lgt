:- object(moviedb_model,
    extends(meta_model)).
    
fact_type(actor, [
(id, id),
(name, atom)
]).

fact_type(movie, [
(id, id),
(name, atom),
(rating, number)
]).    
    
:- end_object.