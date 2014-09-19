:- module(student, []).
:- dynamic(current_id/1).
current_id(3).
:- dynamic(student/3).
student(1, 123, 'Paulchen Panther').
student(2, 999, 'Oma Anneliese').
student(3, 314, 'Mr. Pi').
