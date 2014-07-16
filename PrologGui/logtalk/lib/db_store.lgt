:- object(db_store).

:- public(persist/0).

:- public(get_id/1).

:- public(current_id/1).
:- dynamic(current_id/1).

:- public(get_term/1).

% to be implementend by concrete store
:- public(output_filename/1).

:- private(print_data/0).
:- private(print_id/0).

persist :-
	::output_filename(F),
	user:logtalk_library_path(data_directory, Dir),
	atom_concat(Dir, F, Path),
	user:tell(Path),
	print_id,
	print_data,
	user:told.

print_id :-
	::current_id(Current),
	!,
	user:writeln(':- dynamic(current_id/1).'),
	user:portray_clause(current_id(Current)).
	
% if there is no id (for relations) do noting
print_id.
	
print_data :-
	::get_term(Functor/Arity),
	functor(Term, Functor, Arity),
	::clause(Term, _),
	user:portray_clause(Term),
	fail.
	
print_data.

get_id(Id) :-
	% if there is no current_id, set current to 0
	( ::current_id(Current)
	-> true
	; Current = 0),  
	% delete old entry
	::retractall(current_id(_)),
	% add entry with increased id
	Id is Current + 1,
	::assert(current_id(Id)).


	
:- end_object.