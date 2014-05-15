:- dynamic id_handling:current_id/2.

id_handling:current_id(fsdb_serie,11).

:- dynamic fsdb_serie_data/4.
:- multifile fsdb_serie_data/4.

fsdb_serie_data(1, 'Game of Thrones', 'USA', true).
fsdb_serie_data(4, 'Modern Family', 'USA', false).
fsdb_serie_data(3, 'Lost', 'USA', true).
fsdb_serie_data(2, 'Stromberg', 'Deutschland', false).

:- dynamic fsdb_serie_stats/3.
:- multifile fsdb_serie_stats/3.

fsdb_serie_stats(1, 4, 40).
fsdb_serie_stats(4, 5, 111).
fsdb_serie_stats(3, 6, 124).
fsdb_serie_stats(2, 5, 46).

