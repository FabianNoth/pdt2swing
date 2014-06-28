:- initialization((
%    set_logtalk_flag(events, allow),           % set project-specific flags
    logtalk_load(['../id_handling']),
    logtalk_load([db_controller, db_id_handler, db_store])  % load the project source files
)).