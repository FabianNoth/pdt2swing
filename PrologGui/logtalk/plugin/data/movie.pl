:- module(movie, []).
:- dynamic(current_id/1).
current_id(7).
:- dynamic(movie/3).
movie(4, 'Rocky', 'Action').
movie(1, 'Titanic', 'Drama').
movie(5, 'Saw', 'Horror').
movie(3, 'Spiderman', 'Action').
movie(2, 'Harry Potter', 'Fantasy').
movie(7, 'Shutter Island', '').
