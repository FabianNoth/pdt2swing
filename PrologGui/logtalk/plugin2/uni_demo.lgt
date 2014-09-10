:- object(uni_demo,
    extends(meta_model)).

%%%%%%%%%
% facts %
%%%%%%%%%

fact_type(student, [
	arg(id, id, [main]),
	arg(matrikel, number, [unique]),
	arg(name, atom, [])
]).

fact_type(dozent, [
	arg(id, id, [main]),
	arg(name, atom, [unique]),
	arg(alter, number(18, 99), [])
]).

fact_type(veranstaltung, [
	arg(id, id, [main]),
	arg(name, atom, [unique]),
	arg(type, atom(veranstaltungstyp), []),
	arg(dozent, dozent, [])
]).


%%%%%%%%%
% table %
%%%%%%%%%
%table_display_type(actor, full).
%table_display_type(movie, [id, name]).
%table_display_type(role, full).
%table_display_type(tag, full).


%%%%%%%%%%%%%%
%% relations %
%%%%%%%%%%%%%%

relation_many_type(teilnehmer, [
	arg(id, veranstaltung, []),
	arg(student, student, [])
]).


%%%%%%%%%%%%%%%%
%% fixed atoms %
%%%%%%%%%%%%%%%%

fixed_atom(veranstaltungstyp, ['', 'Praktikum', 'Vorlesung', 'Seminar']).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        relation dummies         %
% (for single & rating relations) %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% bundles %
bundle(student, []).
bundle(dozent, []).
bundle(veranstaltung, [teilnehmer]).

:- end_object.