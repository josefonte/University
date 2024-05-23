-module(user_manager).
-export([start/0,
        loop/1]).



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



%processo servidor
handle({create_album,Album,Username},Pid,Map) ->
    case maps:find(Album,Map) of
        error ->
            NewSet = sets:from_list([Username]),
            NewMap = maps:put(Album,NewSet,Map),
            ?MODULE ! {ok, NewMap,Pid};
        _ ->
            ?MODULE ! {album_exists, Map,Pid}
    end;

handle({verify_user,Album,User},Pid,Map) ->
    case maps:find(Album,Map) of
        {ok,Users} ->
            case sets:is_element(User, Users) of
                true ->
                    ?MODULE ! {ok, Map,Pid};
                false ->
                    ?MODULE ! {user_not_found, Map,Pid}
            end;
        _ ->
            ?MODULE ! {album_not_found, Map,Pid}
    end;

handle({add_user,Album, Username},Pid,Map) ->
    case maps:find(Album,Map) of
        error ->
            ?MODULE ! {album_not_found, Map,Pid};
        {ok,Users} ->
            case sets:is_element(Username, Users) of
                false ->
                    NewUsers = sets:add_element(Username, Users),
                    NewMap = maps:put(Album,NewUsers,Map),
                    ?MODULE ! {ok, NewMap,Pid};
                true ->
                    ?MODULE ! {user_exists, Map,Pid}
            end
    end;

handle({remove_user, Album,Username},Pid,Map) ->
    case maps:find(Album, Map) of
       {ok, Users} ->
           case sets:is_element(Username, Users) of
               true ->
                   NewMap = maps:put(Album, sets:del_element(Username, Users), Map),
                   ?MODULE ! {ok, NewMap,Pid};
               _ ->
                ?MODULE ! {username_not_found, Map,Pid}
           end;
       _ ->
        ?MODULE ! {album_not_found, Map,Pid}
    end;

handle({update_album, Album, Users}, Pid,Map) ->
            case maps:find(Album, Map) of
                error ->
                    ?MODULE ! {album_not_found, Map,Pid};
                {ok, _ } ->
                    NewMap = maps:put(Album, Users, Map),
                    ?MODULE ! {ok, NewMap,Pid}
            end;

handle({get_album_users, Album}, Pid,Map) ->
    case maps:find(Album, Map) of
        {ok, Users} ->
            ?MODULE ! {{ok,Users}, Map,Pid};
        _ ->
            ?MODULE ! {album_not_found, Map,Pid}
    end.
