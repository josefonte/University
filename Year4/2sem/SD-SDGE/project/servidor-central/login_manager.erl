-module(login_manager).
-export([start/0,loop/1,isUserOnline/2]).


start()-> register(?MODULE,spawn(fun() -> loop(#{}) end)).


loop(Users) ->
    receive
        {Request, From} ->
            spawn(fun() -> handle(Request, From, Users) end),
            loop(Users);
        {Msg,UsersNextState,From} ->
            From ! {Msg, ?MODULE},
            loop(UsersNextState)
    end.

handle({create_account, Username, Passwd}, Pid, Users) ->
    case maps:find(Username, Users) of
        error ->
            HashedPasswd = crypto:hash(sha256, Passwd),
            ?MODULE ! {ok, maps:put(Username, {Pid, HashedPasswd, true}, Users),Pid};
        _ ->
            ?MODULE ! {user_exists, Users,Pid}
    end;

handle({close_account, Username, Passwd}, Pid, Users) ->
    case maps:find(Username, Users) of
        {ok, {_, StoredHash, _}} ->
            HashedPasswd = crypto:hash(sha256, Passwd),
            case StoredHash == HashedPasswd of
                true ->
                    ?MODULE ! {ok, maps:remove(Username, Users),Pid};
                false ->
                    ?MODULE ! {invalid, Users,pid}
            end;
        _ ->
            ?MODULE ! {invalid, Users,Pid}
    end;

handle({login, Username, Passwd}, Pid, Users) ->
    case maps:find(Username, Users) of
        {ok, {OldPid, StoredHash, false}} ->
            HashedPasswd = crypto:hash(sha256, Passwd),
            case StoredHash == HashedPasswd of
                true ->
                    ?MODULE ! {ok, maps:update(Username, {OldPid, HashedPasswd, true}, Users),Pid};
                false ->
                    ?MODULE ! {invalid, Users,Pid}
            end;
        _ ->
            ?MODULE ! {invalid, Users,Pid}
    end;

handle({logout, Username}, Pid, Users) ->
    case maps:find(Username, Users) of
        {ok, {OldPid, Passwd, true}} ->
            ?MODULE ! {ok, maps:update(Username, {OldPid, Passwd, false}, Users),Pid};
        _ ->
            ?MODULE ! {invalid, Users,Pid}
    end;

handle(online, Pid, Users) ->
    OnlineUsers = maps:filter(fun(_, {_, _, true}) -> true; (_, _) -> false end, Users),
    OnlineUsernames = maps:keys(OnlineUsers),
    ?MODULE ! {OnlineUsernames, Users,Pid};

handle({verify_user, Username}, Pid, Users) ->
    case maps:find(Username, Users) of
        {ok, _} ->
            ?MODULE ! {ok, Users,Pid};
        _ ->
            ?MODULE ! {invalid, Users,Pid}
    end.


isUserOnline(Username, Users) ->
    case maps:find(Username, Users) of
        {ok, {_, true}} ->
            true;
        _ ->
            false
    end.
