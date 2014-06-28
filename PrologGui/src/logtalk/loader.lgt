:- initialization((
%    set_logtalk_flag(events, allow),           % set project-specific flags
    logtalk_load([gui_hooks, id_handling, output, utils])  % load the project source files
)).