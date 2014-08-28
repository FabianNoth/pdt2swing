:- object(actor_store,
    extends(db_store)).

:- public(actor/2).
:- use_module('c:/users/fabian/git/pdt2swing/prologgui/logtalk/plugin/data/actor.pl').

output_filename('c:/users/fabian/git/pdt2swing/prologgui/logtalk/plugin/data/actor.pl').

get_term(actor/2).

module_name(actor).

actor(A,B) :- actor:actor(A,B).

current_id(Id) :- actor:current_id(Id).

:- end_object.