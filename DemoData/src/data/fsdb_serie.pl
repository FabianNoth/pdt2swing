:- dynamic id_handling:current_id/2.

id_handling:current_id(fsdb_serie,3).

:- dynamic fsdb_serie_data/3.
:- multifile fsdb_serie_data/3.

fsdb_serie_data(1, 'Game of Thrones', 'USA').
fsdb_serie_data(2, 'Stromberg', 'Deutschland').
fsdb_serie_data(3, 'Lost', 'USA').

:- dynamic fsdb_serie_stats/3.
:- multifile fsdb_serie_stats/3.

fsdb_serie_stats(1, 4, 40).
fsdb_serie_stats(2, 5, 46).
fsdb_serie_stats(3, 6, 121).

