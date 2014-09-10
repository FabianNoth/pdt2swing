:- module(role, []).
:- dynamic(current_id/1).
current_id(4).
:- dynamic(role/4).
role(2, 2, 1, 'Jack Dawson').
role(1, 1, 1, 'Rose').
role(3, 4, 4, 'Rocky Balboa').
role(4, 8, 9, 'Forrest Gump').
