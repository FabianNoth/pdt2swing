:- object(db_store).

:- public(add/1).
:- public(delete/1).

:- public(persist/0).

:- public(get_id/1).

:- public(current_id/1).

:- public(get_term/1).

% to be implementend by concrete store
:- public(output_filename/1).
:- public(module_name/1).

:- private(print_data/0).
:- private(print_id/0).

persist :-
	::output_filename(F),
	user:logtalk_library_path(data_directory, Dir),
	atom_concat(Dir, F, Path),
	user:tell(Path),
	print_module_head,
	print_id,
	print_data,
	user:told.

print_module_head :-
	::get_term(Functor/_),
	atomic_list_concat([':- module(', Functor, ', []).'], ModuleHead),
	user:writeln(ModuleHead).
	
print_id :-
	::current_id(Current),
	!,
	user:writeln(':- dynamic(current_id/1).'),
	user:portray_clause(current_id(Current)).
	
% if there is no id (for relations) do noting
print_id.
	
print_data :-
	::get_term(Functor/Arity),
	::module_name(Module),
	atomic_list_concat([':- dynamic(', Functor, '/', Arity, ').'], DynamicDecl),
	user:writeln(DynamicDecl),
	functor(Term, Functor, Arity),
	Module:clause(Term, _),
	user:portray_clause(Term),
	fail.
	
print_data.

get_id(Id) :-
	% if there is no current_id, set current to 0
	( ::current_id(Current)
	-> true
	; Current = 0),
	% delete old entry
	::delete(current_id(_)),
	% add entry with increased id
	Id is Current + 1,
	::add(current_id(Id)).


add(Term) :-
	::module_name(Module),
	Module:assert(Term).
	
delete(Term) :-
	::module_name(Module),
	Module:retractall(Term).
	
current_id(Id) :-
	::module_name(Module),
	Module:catch(current_id(Id), _, fail).
	
:- end_object.