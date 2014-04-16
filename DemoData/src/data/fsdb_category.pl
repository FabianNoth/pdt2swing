:- dynamic id_handling:current_id/2.

id_handling:current_id(fsdb_category,4).

:- dynamic fsdb_category/2.
:- multifile fsdb_category/2.

fsdb_category(1, 'Sitcom').
fsdb_category(2, 'Action').
fsdb_category(3, 'Fantasy').
fsdb_category(4, 'Mystery').

