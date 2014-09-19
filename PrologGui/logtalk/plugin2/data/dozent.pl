:- module(dozent, []).
:- dynamic(current_id/1).
current_id(2).
:- dynamic(dozent/3).
dozent(1, 'Dr. Hasenbein', 20).
dozent(2, 'Prof. Gustav Gans', 44).
