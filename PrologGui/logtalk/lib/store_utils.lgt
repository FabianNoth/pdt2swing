:- object(store_utils).

:- public(create_data_stores/1).
:- private(create_data_store/1).

create_data_stores(Model) :-
	forall( (Model::fact_type(Functor, Args), length(Args, Arity)),
			create_data_store(Functor/Arity)).
	
create_data_store(Functor/Arity) :-
	atom_concat(Functor, '_store', StoreName),
	atom_concat(Functor, '.pl', FileName),
	create_object(	StoreName,
					[extends(db_store)],
					[public(Functor/Arity), dynamic(Functor/Arity), include(data_directory(FileName))],
					[output_filename(FileName), get_term(Functor/Arity)]).
	
:- end_object.