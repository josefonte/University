-module(file_manager).
-export([start/0]).



start()-> register(?MODULE,spawn(fun() -> loop(#{}) end)).

loop(Map) ->
    receive
        {Request, From} ->
            spawn(fun() -> handle(Request, From, Map) end),
            loop(Map);
        {Msg,NewMap,From} ->
            From ! {Msg, ?MODULE},
            loop(NewMap)
    end.






add_files(OldFiles, Files) ->
    NewFiles = maps:fold(
        fun(Name, Ratings, Acc) ->
            case maps:find(Name, Files) of
                {ok, NewRatings} ->
                    MergedRatings = maps:merge(Ratings,NewRatings),
                    maps:put(Name, MergedRatings, Acc);   
                error ->
                    Acc
            end
        end,
        #{},
        OldFiles
    ),
    maps:fold(
        fun(Name, Ratings, Acc) ->
            case maps:find(Name, OldFiles) of
                error ->
                    maps:put(Name, Ratings, Acc);
                _ ->
                    Acc  
            end        
        end,
        NewFiles,
        Files
    ).



handle({create_album,Album},Pid,Map) ->
    case maps:find(Album,Map) of
        error ->
            NewMap = maps:put(Album,#{},Map),
            ?MODULE ! {ok, NewMap,Pid};
        _ ->
            ?MODULE ! {album_exists, Map,Pid}
    end;


handle({get_album,Album},Pid,Map) ->
    case maps:find(Album,Map) of
        {ok,Files} ->
            ?MODULE ! {{ok,Files}, Map,Pid};
        _ ->
            ?MODULE ! {album_not_found, Map,Pid}
    end;
        
handle({check_file,Album,Name},Pid,Map) ->
    case maps:find(Album,Map) of
        {ok,Files} ->
            case maps:find(Name,Files) of
                {ok,_} ->
                    ?MODULE ! {ok, Map,Pid};
                _ ->
                    ?MODULE ! {file_not_found, Map,Pid}
            end;
        _ ->
            ?MODULE ! {album_not_found, Map,Pid}
    end;
    
handle({get_files,Album},Pid,Map) ->
    case maps:find(Album,Map) of
        error ->
            ?MODULE ! {album_not_found, Map,Pid};
        {ok,Files} ->
            NewFiles = maps:fold(
                fun(Name, Rates, Acc) ->
                    Average = case maps:size(Rates) of
                        0 -> 0; % Return 0 if Rates is empty
                        _ -> lists:sum(maps:values(Rates))/maps:size(Rates)
                    end,
                    maps:put(Name, Average, Acc)
                end,
                #{},
                Files
            ),
            ?MODULE ! {{ok,NewFiles}, Map,Pid}
    end;

handle({add_file,Album, Name},Pid,Map) ->
    % ir buscar map daquele album
    case maps:find(Album,Map) of
        error ->
            ?MODULE ! {album_not_found, Map,Pid};
        {ok,Files} ->
            case maps:find(Name, Files) of
                error ->
                    NewFiles = maps:put(Name,#{},Files),
                    NewMap = maps:put(Album,NewFiles,Map),
                    ?MODULE ! {ok, NewMap,Pid};
                {ok,_} ->
                    ?MODULE ! {filename_exists, Map,Pid}
            end
    end;

handle({remove_file, Album,Name},Pid,Map) ->
    case maps:find(Album, Map) of
        error ->
            ?MODULE ! {album_not_found, Map,Pid};
        {ok, Files} ->
            case maps:find(Name, Files) of
                {ok, _} ->
                    NewMap = maps:put(Album, maps:remove(Name, Files), Map),
                    ?MODULE ! {ok, NewMap,Pid};
                _ ->
                    ?MODULE ! {file_not_found, Map,Pid}
                end
    end;

handle({rate_file,Album, Name,Rate,Username},Pid,Map) ->
    case string:to_integer(Rate) of
        {error, _} ->
            % Conversion failed, Reason contains the error reason
            ?MODULE ! {number_format_error, Map,Pid};
        {RateInt, _} ->
            case maps:find(Album, Map) of
                error ->
                    ?MODULE ! {album_not_found, Map,Pid};
                {ok, Files} ->
                    case maps:find(Name, Files) of
                        {ok, Rates} ->
                            case maps:find(Username, Rates) of
                                error ->
                                    NewRates = maps:put(Username, RateInt, Rates),
                                    NewFiles = maps:put(Name,  NewRates, Files),
                                    NewMap = maps:put(Album, NewFiles, Map),
                                    ?MODULE ! {ok, NewMap,Pid};
                                _ ->
                                    ?MODULE ! {file_already_rated, Map,Pid}
                            end;
                        _ ->
                            ?MODULE ! {file_not_found, Map,Pid}
                    end
            end
    end;

handle({update_album, Album, AllFiles}, Pid,Map)->
    case maps:find(Album, Map) of
        error ->
            ?MODULE ! {album_not_found, Map,Pid};
        {ok, OldFiles} ->
            io:format("file_manager: OldFiles: ~p~n", [OldFiles]),
            io:format("file_manager: AllFiles: ~p~n", [AllFiles]),
            NewFiles = add_files(OldFiles, AllFiles),
            io:format("file_manager: NewFiles: ~p~n", [NewFiles]),
            NewMap = maps:put(Album, NewFiles, Map),
            ?MODULE ! {ok, NewMap,Pid}
    end;

handle({get_album_files, Album, User}, Pid,Map) ->
            case maps:find(Album, Map) of
                error ->
                    ?MODULE ! {album_not_found, Map,Pid};
                {ok, Files} ->
                    FilteredFiles = maps:map(
                        fun(_File, Ratings) ->
                            case maps:find(User, Ratings) of
                                {ok, Rating} ->
                                    case is_integer(Rating) of
                                        true -> integer_to_list(Rating);
                                        false -> "null"
                                    end;
                                error -> "null"
                            end
                        end, Files),
                    ?MODULE ! {{ok,FilteredFiles}, Map,Pid}
            end.
