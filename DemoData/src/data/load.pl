:- consult('fsdb_serie.pl').
:- consult('fsdb_tags.pl').
:- consult('fsdb_serie_rating.pl').
:- consult('fsdb_category.pl').

%:- multifile add_data_hook/2.
%:- multifile remove_data_hook/1.
%:- multifile update_data_hook/1.
%:- multifile output_data_hook/2.
%:- multifile check_for_existing_value_hook/2.

fsdb_serie_display(Id, Title, Nation, Seasons, Episodes, Rating) :-
	user:fsdb_serie_data(Id, Title, Nation, _),
	user:fsdb_serie_stats(Id, Seasons, Episodes),
	user:fsdb_serie_rating(Id, RatingTerm),
	RatingTerm =.. [_, Rating].
	
fsdb_serie(Id, Title, Nation, Seasons, Episodes, Epic) :-
	user:fsdb_serie_data(Id, Title, Nation, Epic),
	user:fsdb_serie_stats(Id, Seasons, Episodes).
	
fsdb_tags(Id, Tag) :-
	user:fsdb_tags_impl(Id, TagId),
	user:fsdb_category(TagId, Tag).

:- multifile gui_hooks:add_data_hook/2.
gui_hooks:add_data_hook(fsdb_serie(AddedId, Title, Nation, Seasons, Episodes, Epic), AddedId) :-
	id_handling:new_id(fsdb_serie, AddedId),
	assert(user:fsdb_serie_data(AddedId, Title, Nation, Epic)),
	assert(user:fsdb_serie_stats(AddedId, Seasons, Episodes)),
	assert(user:fsdb_serie_rating(AddedId, u(0))).
	
gui_hooks:add_data_hook(fsdb_tags(Id, Tag), _) :-
	( user:fsdb_category(TagId, Tag)
	-> true
	; add_fact(fsdb_category(TagId, Tag), TagId) ),
	Goal = fsdb_tags_impl(Id, TagId),
	ground(Goal),
	( call(user:Goal)
	-> true
	;  assert(user:Goal)).
	
:- multifile gui_hooks:remove_data_hook/1.

gui_hooks:remove_data_hook(fsdb_serie(Id, _, _, _, _, _)) :-
	retractall(user:fsdb_serie_data(Id, _, _, _)),
	retractall(user:fsdb_serie_stats(Id, _, _)),
	retractall(user:fsdb_serie_rating(Id, _)),
	% relations
	retractall(user:fsdb_tags_impl(Id, _)).

gui_hooks:remove_data_hook(fsdb_tags(Id, Tag)) :-
	user:fsdb_category(TagId, Tag),
	retractall(user:fsdb_tags_impl(Id, TagId)).
	
gui_hooks:remove_data_hook(fsdb_category(Id, _)) :-
	retractall(user:fsdb_category(Id, _)),
	retractall(user:fsdb_tags_impl(_, Id)).
	
:- multifile gui_hooks:update_data_hook/1.

gui_hooks:update_data_hook(fsdb_serie(Id, Title, Nation, Seasons, Episodes, Epic)) :-
	retractall(user:fsdb_serie_data(Id, _, _, _)),
	retractall(user:fsdb_serie_stats(Id, _, _)),
	assert(user:fsdb_serie_data(Id, Title, Nation, Epic)),
	assert(user:fsdb_serie_stats(Id, Seasons, Episodes)).

:- multifile gui_hooks:output_data_hook/2.
gui_hooks:output_data_hook(fsdb_serie, 6) :-
	listing(user:fsdb_serie_data/4),
	listing(user:fsdb_serie_stats/3).
	
gui_hooks:output_data_hook(fsdb_tags, 2) :-
	listing(user:fsdb_tags_impl/2).

:- multifile gui_hooks:check_for_existing_value_hook/2.
gui_hooks:check_for_existing_value_hook(fsdb_tags, Value) :-
	user:fsdb_category(_, Value).
	
:- multifile gui_hooks:auto_completion_hook/2.
gui_hooks:auto_completion_hook(fsdb_tags, Value) :-
	user:fsdb_category(_, Value).
