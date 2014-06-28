:- object(db_store).

:- public(get_id/1).

:- public(current_id/1).
:- dynamic(current_id/1).

:- public(get_term/1).
:- public(output_file/1).

:- public(persist/0).
:- private(print/0).

persist :-
	::output_file(F),
	user:file_search_path(data_directory, Dir),
	atom_concat(Dir, F, Path),
%	writeln(Path).
	tell(Path),
	print,
	told.

print :-
	::get_term(Functor/Arity),
	functor(Term, Functor, Arity),
	::clause(Term, _),
	user:portray_clause(Term),
	fail.
	
print.

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