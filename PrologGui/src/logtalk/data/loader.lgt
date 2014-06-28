:- initialization((
%    set_logtalk_flag(events, allow),           % set project-specific flags
%    logtalk_load(['../id_handling']),
%    logtalk_load([db_store, controller, db_id_handler, db_store])  % load the project source files
	assert(user:file_search_path(data_directory,'c:/Users/Fabian/git/pdt2swing/PrologGui/src/logtalk/data/')),

	logtalk_load([db_store, db_controller, actor_store])
)).