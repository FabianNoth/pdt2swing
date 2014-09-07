:- module(role, []).
:- dynamic(current_id/1).
current_id(2).
:- dynamic(role/4).
role(2, 2, 1, 'Jack Dawson').
role(1, 1, 1, 'Rose').
