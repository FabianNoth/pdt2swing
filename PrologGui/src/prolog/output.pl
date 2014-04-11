:- module(output, [
	output_to_file/2,
	output_to_file_plus_id/2
]).

output_to_file(File, Goal) :-
	tell(File),
	listing(Goal),
	told.

output_to_file_plus_id(File, Goal) :-
	tell(File),
	listing(Goal),
	user:listing(current_id/1),
	told.

% TODO: use qlf files	