:- initialization((
	assert(user:logtalk_library_path(data_directory,'c:/Users/Finko/git/pdt2swing/PrologGui/logtalk/plugin/data/')),

	% load meta model
	logtalk_load([moviedb_model]),
	
	% load controller
	db_controller::init_model(moviedb_model),
	% create store objects from meta model
	store_utils::create_data_stores(moviedb_model)
)).

% This is how the objects look like
%:- object(movie_store,
%    extends(db_store)).
%    
%	%% movie(Id, Name)	
%	:- public(movie/2).
%	:- dynamic(movie/2).
%	:- include('data/movie.pl').
%	
%	output_filename('movie.pl').
%	get_term(movie/2).
%	
%:- end_object.