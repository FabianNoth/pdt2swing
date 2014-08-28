:- module(movie, []).
:- dynamic(current_id/1).
current_id(6).
:- dynamic(movie/3).
movie(2, 'Harry Potter', '').
movie(3, 'Spiderman', '').
movie(4, 'Rocky', 'Action').
movie(1, 'Titanic', 'Drama').
movie(5, 'Saw', 'Horror').
