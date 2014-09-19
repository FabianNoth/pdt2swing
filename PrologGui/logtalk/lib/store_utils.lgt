:- object(store_utils).

:- public(create_data_stores/1).
:- private(create_data_store/1).

create_data_stores(Model) :-
	forall( (Model::element(Functor, Args), length(Args, Arity)),
			create_data_store(Functor/Arity)).
	
create_data_store(Functor/Arity) :-
	atom_concat(Functor, '_store', StoreName),
	atom_concat(Functor, '.pl', FileName),
	user:logtalk_library_path(data_directory,Dir),
	atom_concat(Dir, FileName, FullFilePath),
	create_file_if_necessary(FullFilePath),
	use_module(FullFilePath, []),	% <-- works
	functor(MyTerm, Functor, Arity),
%	db_controller::get_table_predicate(Functor, TableHead, TableBody),
	create_object(	StoreName,
					[extends(db_store)],
					[public(Functor/Arity)],% use_module(FullFilePath, [])],		% <-- doesn't work
%					[public(Functor/Arity), public(assertf/1), public(retractallf/1), use_module(FileName)],
%					[public(Functor/Arity), dynamic(Functor/Arity), include(data_directory(FileName))],
					[output_filename(FileName),
					get_term(Functor/Arity),
					module_name(Functor),
%					((MyTerm :- Functor:MyTerm))
					((MyTerm :- catch(Functor:MyTerm, _, fail)))
%					((TableHead :- TableBody))
%					((current_id(Id) :- Functor:current_id(Id)))
%					((assertf(Term) :- Functor:assert(Term))),
%					((retractallf(Term) :- Functor:retractall(Term))),
%					((current_id(Id) :- Functor:current_id(Id)))
					%((MyTerm :- Functor:MyTerm))
					]).
%:- public(test_me/0).
%test_me :-
%	StoreName = actor_store7,
%	Functor = actor,
%	Arity = 2,
%	FileName = 'c:/users/fabian/git/pdt2swing/prologgui/logtalk/plugin/data/actor.pl',
%	MyTerm = actor(_,_),
%
%%	create_object(StoreName, [extends(db_store)],[public(Functor/Arity)],[]).
%	create_object(	StoreName,
%					[extends(db_store)],
%					[public(Functor/Arity), use_module(FileName, [])],
%					[output_filename(FileName),
%					get_term(Functor/Arity),
%					module_name(Functor),
%					((MyTerm :- Functor:MyTerm)),
%					((current_id(Id) :- Functor:current_id(Id)))
%					]).
	
create_file_if_necessary(File) :-
	exists_file(File), !.

create_file_if_necessary(File) :-
	tell(File),
	told.
		
	
	
:- end_object.