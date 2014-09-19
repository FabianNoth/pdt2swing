:- module(veranstaltung, []).
:- dynamic(current_id/1).
current_id(2).
:- dynamic(veranstaltung/4).
veranstaltung(1, 'Grundlagen der Programmierung', 'Vorlesung', 1).
veranstaltung(2, 'Tolles Seminar', 'Seminar', 2).
