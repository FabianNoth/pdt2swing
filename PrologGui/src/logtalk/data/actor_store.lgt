:- object(actor_store,
    extends(db_store)).
    
%% actor(Id, Name)	
	:- public(actor/2).
	:- dynamic(actor/2).
	:- include('actor.pl').
	
	output_file('actor2.pl').
	get_term(actor/2).
	
:- end_object.