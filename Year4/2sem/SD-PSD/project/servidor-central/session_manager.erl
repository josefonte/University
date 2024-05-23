-module(session_manager).
-export([start/0, loop/2]).


start() -> register(?MODULE, spawn(fun() -> loop([], #{}) end)).


add_files(OldFiles, NewFiles, User) ->
    maps:fold(fun(File, Rating, Acc) ->
                    checkRatingAndUpdate(Acc, File, Rating, User) end, OldFiles, NewFiles).


checkRatingAndUpdate(OldFiles, File, Rating, User) ->
    case Rating of
        "null" ->
            add_file_empty(OldFiles,File);
        _ ->
            case string:to_integer(Rating) of
                {error, _} ->
                    add_file_empty(OldFiles,File);
                {RatingInt, _} -> update_file(OldFiles, File, RatingInt, User)
            end
    end.


add_file_empty(OldFiles, File) ->
    case maps:is_key(File, OldFiles) of
        true ->
            OldFiles;
        false ->
            maps:put(File, #{}, OldFiles)
    end.

update_file(OldFiles, File, Rating, User) ->
    case maps:is_key(File, OldFiles) of
        true ->
            OldFileRatings = maps:get(File, OldFiles),
            case maps:is_key(User, OldFileRatings) of
                true ->
                    % Verify if value is null and if is update it
                    case maps:get(User, OldFileRatings) of
                        "null" ->
                            NewRatings = maps:put(User, Rating, OldFileRatings),
                            maps:put(File, NewRatings, OldFiles);
                        _ ->
                            % continue with the old value
                            OldFiles
                    end;
                false ->
                    NewRatings = maps:put(User, Rating, OldFileRatings),
                    maps:put(File, NewRatings, OldFiles)
            end;
        false ->
            NewRatings = #{User => Rating},
            maps:put(File, NewRatings, OldFiles)
    end.


loop(Users, Albums) ->
    receive
        {Request, From} ->
            spawn(fun() -> handle(Request, From, Users, Albums) end),
            loop(Users, Albums);
        {Msg,UsersNextState,AlbumsNextState,From} ->
            From ! {Msg, ?MODULE},
            loop(UsersNextState, AlbumsNextState)
    end.

handle({session_join, User}, Pid, Users, Albums) ->
    case lists:member(User, Users) of
        false ->
            ?MODULE ! {ok, [User | Users], Albums,Pid};
        true ->
            ?MODULE ! {user_exists, Users, Albums,Pid}
    end;

handle({session_leave, User}, Pid, Users, Albums) ->
    case lists:member(User, Users) of
        true ->
            List = lists:delete(User, Users),
            io:format("session_manager: UsersOnSession: ~p~n", [List]),
            ?MODULE ! {ok, List, Albums,Pid};
            % {ok, lists:delete(User, Users), Albums};
        false ->
            ?MODULE ! {user_not_found, Users, Albums,Pid}
    end;

handle({is_last_user, User}, Pid, Users, Albums) ->
    io:format("session_manager: UsersOnSession: ~p~n", [Users]),
    case length(Users) of
        1 ->
            case lists:member(User, Users) of
                true ->
                    ?MODULE ! {ok, Users, Albums,Pid};
                false ->
                    ?MODULE ! {user_not_last, Users, Albums,Pid}
            end;
        _ ->
            ?MODULE ! {user_not_last, Users, Albums,Pid}
    end;

handle({update_ratings, User, Album, Files}, Pid, Users, Albums) ->
    case lists:member(User, Users) of
        true ->
            case maps:find(Album, Albums) of
                {ok, OldFiles} ->
                    io:format("session_manager: OldFiles: ~p~n", [OldFiles]),
                    NewFiles = add_files(OldFiles, Files, User),
                    io:format("session_manager: NewFiles: ~p~n", [NewFiles]),
                    NewAlbum = maps:put(Album, NewFiles, Albums),
                    ?MODULE ! {ok, Users, NewAlbum,Pid};
                error ->
                    NewFiles = add_files(#{}, Files, User),
                    io:format("session_manager: NewAlbum: ~p~n", [NewFiles]),
                    NewAlbum = maps:put(Album, NewFiles, Albums),
                    ?MODULE ! {ok, Users, NewAlbum,Pid}
            end;
        false ->
            ?MODULE ! {user_not_found, Users, Albums,Pid}
    end;

handle({update_and_get_all_files, User, Album, Files}, Pid, Users, Albums) ->
    case lists:member(User, Users) of
        true ->
            case maps:find(Album, Albums) of
                {ok, OldFiles} ->
                    io:format("session_manager: OldFiles: ~p~n", [OldFiles]),
                    NewFiles = add_files(OldFiles, Files, User),
                    io:format("session_manager: NewFiles: ~p~n", [NewFiles]),
                    NewAlbum = maps:put(Album, NewFiles, Albums),
                    ?MODULE ! {{ok, NewFiles}, Users, NewAlbum,Pid};
                error ->
                    NewFiles = add_files(#{}, Files, User),
                    io:format("session_manager: NewAlbum: ~p~n", [NewFiles]),
                    NewAlbum = maps:put(Album, NewFiles, Albums),
                    ?MODULE ! {{ok, NewFiles}, Users, NewAlbum,Pid}
            end;
        false ->
            ?MODULE ! {user_not_found, Users, Albums,Pid}
    end.


% handle({get_all_ratings, Album}, _, Users, Albums) ->
%     io:format("session_manager: get_all_ratings"),
%     case maps:find(Album, Albums) of
%         {ok, Files} ->
%             {{ok, Files}, Users, Albums};
%         error ->
%             {album_not_found, Users, Albums}
%     end.
