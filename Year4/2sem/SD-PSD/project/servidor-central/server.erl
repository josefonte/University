-module(server).
-import(user_manager, []).
-import(file_manager, []).
-import(login_manager, []).
-import(session_manager, []).
-export([start/1, stop/1, server/1]).

start(Port) -> register(?MODULE, spawn(fun() -> server(Port) end)).


server(Port) ->
    Result = gen_tcp:listen(Port, [binary, {packet, line}]),
    case Result of
        {ok, ListenSocket} ->
            user_manager:start(),
            file_manager:start(),
            login_manager:start(),
            session_manager:start(),
            acceptor(ListenSocket,Port);

        {error, _} ->
            stop(Port),
            io:fwrite("Error starting server!\n")
    end.

stop(Port) -> gen_tcp:close(Port).


acceptor(ListenSocket,Port) ->
    case gen_tcp:accept(ListenSocket) of
        {ok, Socket} ->
            spawn(fun() -> acceptor(ListenSocket,Port) end),
            userAuth(Socket,"");

        {error, closed} ->
            stop(Port),
            io:fwrite("Closed socket\n");

        {error, timeout} ->
            stop(Port),
            io:fwrite("Timeout\n");

        {error, system_limit} ->
            stop(Port),
            io:fwrite("Limit of socket reached\n");

        {error, _} ->
            stop(Port),
            io:fwrite("Error listening to socket\n")
    end.

verifyUser(Album,User) ->
    user_manager ! {{verify_user, Album, User}, self()},
    receive
        {ok, _} -> true;
        _ -> false
    end.

userExists(Username) ->
    login_manager ! {{verify_user, Username}, self()},
    receive
        {ok, _} -> true;
        _ -> false
    end.

isLastUserOnSession(User) ->
    session_manager ! {{is_last_user, User}, self()},
    receive
        {ok, _} -> true;
        _ -> false
    end.


% "[nuno|alice]" -> ["nuno", "alice"]
parse_users(UsersData) ->
    CleanData = string:replace(string:replace(UsersData, "[", "", all), "]", "", all),
    UsersList = re:split(CleanData, "\\|", [{return, list}]),
    Users = lists:map(fun(User) -> string:strip(User) end, UsersList),
    sets:from_list(Users).

% "{file=>10|file2=>null}" -> {file, 10}, {file2, null}
parse_files_ratings(FilesData) ->
    CleanData = lists:flatten(string:replace(string:replace(FilesData, "{", "", all), "}", "", all)),
    io:format("CleanData: ~p~n", [CleanData]),
    case CleanData of
        "" ->
            maps:new();
        _ ->
            FilesList = re:split(CleanData, "\\|", [{return, list}]),
            Files = lists:map(fun(File) ->
                [NamePart , RatingsPart] = re:split(File, "=>", [{return, list}]),
                Name = string:strip(NamePart),
                Rating = string:strip(RatingsPart),
                {Name, Rating}
            end, FilesList),
            maps:from_list(Files)
    end.


userAuth(Socket,User) ->
    receive
        {_, _, Data} ->
            CleanedData = re:replace(Data, "\\n|\\r", "", [global,{return,list}]),
            Info = string:split(CleanedData, ",", all),
            io:format("Info: ~p~n", [Info]),
            case Info of
                ["create_account", Username, Passwd] ->
                    login_manager ! {{create_account, Username, Passwd}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Account created\n"),
                            userAuth(Socket,Username);
                        {user_exists, _} ->
                            gen_tcp:send(Socket, "User already exists\n"),
                            userAuth(Socket,User)
                    end;

                ["close_account", Username, Passwd] ->
                    login_manager ! {{close_account, Username, Passwd}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Account closed\n"),
                            userAuth(Socket,"");
                        {invalid, _} ->
                            gen_tcp:send(Socket, "Invalid account\n"),
                            userAuth(Socket,User)
                    end;

                ["login", Username, Passwd] ->
                    login_manager ! {{login, Username, Passwd}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Logged in\n"),
                            userAuth(Socket,Username);
                        {invalid, _} ->
                            gen_tcp:send(Socket, "Invalid login\n"),
                            userAuth(Socket,User)
                    end;

                ["logout"] ->
                    login_manager ! {{logout, User}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Logged out\n"),
                            userAuth(Socket,"");
                        {invalid, _} ->
                            gen_tcp:send(Socket, "Invalid logout\n"),
                            userAuth(Socket,User)

                    end;

                ["online"] ->
                    login_manager ! {online, self()},
                    receive
                        {OnlineUsers, _} ->
                            gen_tcp:send(Socket, "Online users: " ++ string:join(OnlineUsers, ", ") ++ "\n")
                    end,
                    userAuth(Socket,User);

                ["create_album",Album] ->
                    case User of
                        "" ->
                            gen_tcp:send(Socket, "You must be logged in to create an album\n");
                        _ ->
                            file_manager ! {{create_album,Album},self()},
                            user_manager ! {{create_album,Album,User},self()},
                            receive
                                {ok, _} ->
                                    receive
                                        {ok, _} -> 
                                            gen_tcp:send(Socket, "Album created\n");
                                        {album_exists, _} ->
                                            gen_tcp:send(Socket, "Album already exists\n")
                                    end;
                                {album_exists, _} -> gen_tcp:send(Socket, "Album already exists\n")
                            end     
                    end,
                    userAuth(Socket,User);

                ["check_user", Album] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to edit it\n");
                        true ->
                            gen_tcp:send(Socket,"OK\n")
                    end,
                    userAuth(Socket,User);
                
                ["check_file",Album,File] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to download a file from it\n");
                        true ->
                            file_manager ! {{check_file, Album, File}, self()},
                            receive
                                {ok, _} ->
                                    gen_tcp:send(Socket, "OK\n");
                                {file_not_found, _} ->
                                    gen_tcp:send(Socket, "File not found\n");
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User); 

                ["get_files", Album] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to get its content\n");
                        true ->
                            file_manager ! {{get_files, Album}, self()},
                            receive
                                {{ok, Files_Info},file_manager} ->
                                    R = io_lib:format("~p\n", [Files_Info]),
                                    gen_tcp:send(Socket, R);
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["add_file", Album,File] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to add a file\n");
                        true ->
                            file_manager ! {{add_file, Album,File}, self()},
                            receive
                                {ok, _} ->
                                    gen_tcp:send(Socket, "File added\n");
                                {filename_exists, _} ->
                                    gen_tcp:send(Socket, "File name already exists in album\n");
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["remove_file",Album,File] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to remove a file\n");
                        true ->
                            file_manager ! {{remove_file, Album, File}, self()},
                            receive
                                {ok, _} ->
                                    gen_tcp:send(Socket, "File removed\n");
                                {file_not_found, _} ->
                                    gen_tcp:send(Socket, "File not found\n");
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["rate_file",Album,File,Rate] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to rate a file\n");
                        true ->
                            file_manager ! {{rate_file, Album, File, Rate,User}, self()},
                            receive
                                {ok, file_manager} ->
                                    gen_tcp:send(Socket, "File rated\n");
                                {file_not_found, file_manager} ->
                                    gen_tcp:send(Socket, "File not found\n");
                                {album_not_found, file_manager} ->
                                    gen_tcp:send(Socket, "Album not found\n");
                                {file_already_rated, file_manager} ->
                                    gen_tcp:send(Socket, "File already rated\n");
                                {number_format_error, file_manager} ->
                                    gen_tcp:send(Socket, "Rate must be a number\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["get_album",Album] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket,"You must be a user of the album to get its content\n");
                        true ->
                            file_manager ! {{get_album, Album}, self()},
                            receive
                                {{ok, Album_Info},file_manager} ->
                                    R = io_lib:format("~p\n", [Album_Info]),
                                    gen_tcp:send(Socket, R);
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["get_album_info", Album] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to get its info\n");
                        true ->
                            user_manager ! {{get_album_users, Album}, self()},
                            receive
                                {{ok, Users},user_manager} ->
                                    UsersMsg = io_lib:format("~p\n", [sets:to_list(Users)]),
                                    gen_tcp:send(Socket, UsersMsg);
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end,
                            file_manager ! {{get_album_files, Album, User}, self()},
                            receive
                                {{ok, Files},file_manager} ->
                                    io:format("Users: ~p~n", [Files]),
                                    FilesMsg = io_lib:format("~p\n", [Files]),
                                    gen_tcp:send(Socket, FilesMsg);
                                {album_not_found, _} ->
                                    gen_tcp:send(Socket, "Album not found\n")
                            end
                    end,
                    userAuth(Socket,User);

                ["add_user", Album, Username] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to add a user\n");
                        true ->
                            case userExists(Username) of
                                false ->
                                    gen_tcp:send(Socket, "You must enter a valid username to add the user to the album\n");
                                true ->
                                    user_manager ! {{add_user, Album, Username}, self()},
                                    receive
                                        {ok, _} ->
                                            gen_tcp:send(Socket, "User added\n");
                                        {username_exists, _} ->
                                            gen_tcp:send(Socket, "Username already exists in album\n");
                                        {album_not_found, _} ->
                                            gen_tcp:send(Socket, "Album not found\n")
                                    end
                            end
                    end,
                    userAuth(Socket,User);

                ["remove_user", Album,Username] ->
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to remove a user\n");
                        true ->
                            case User == Username of
                                true ->
                                    gen_tcp:send(Socket, "You cannot remove yourself from the album\n");
                                false ->
                                    user_manager ! {{remove_user, Album, Username}, self()},
                                    receive
                                        {ok, _} ->
                                            gen_tcp:send(Socket, "User removed\n");
                                        {username_not_found, _} ->
                                            gen_tcp:send(Socket, "Username not found in album\n");
                                        {album_not_found, _} ->
                                            gen_tcp:send(Socket, "Album not found\n")
                                    end
                            end
                    end,
                    userAuth(Socket,User);

                ["update_album", Album, Users, Files] ->
                    ParsedUsers = parse_users(Users),
                    io:format("Files: ~p~n", [Files]),
                    ParsedFiles = parse_files_ratings(Files),
                    io:format("ParsedFiles: ~p~n", [ParsedFiles]),
                    case verifyUser(Album,User) of
                        false ->
                            gen_tcp:send(Socket, "You must be a user of the album to update it\n");
                        true ->
                            case isLastUserOnSession(User) of
                                false ->
                                    io:format("Updating ratings on session_manager\n"),
                                    session_manager ! {{update_ratings, User, Album, ParsedFiles}, self()},
                                    receive
                                        {ok, _} ->
                                            gen_tcp:send(Socket, "Ratings updated\n");
                                        {album_not_found, _} ->
                                            gen_tcp:send(Socket, "Album not found\n")
                                    end;
                                true ->
                                    io:format("IM LAST USER AND IM GOING TO UPDATE ALBUM\n"),
                                    user_manager ! {{update_album, Album, ParsedUsers}, self()},
                                    receive
                                        {ok, _} ->
                                            io:format("Updating ratings on session_manager\n"),
                                            session_manager ! {{update_and_get_all_files, User, Album, ParsedFiles}, self()},
                                            receive
                                                {{ok, AllFiles},session_manager} ->
                                                    io:format("all ratings: ~p~n", [AllFiles]);
                                                {album_not_found, _} ->
                                                    AllFiles = #{}
                                            end,
                                            io:format("Updating files on file_manager\n"),
                                            file_manager ! {{update_album, Album, AllFiles}, self()},
                                            receive
                                                {ok, _} ->
                                                    gen_tcp:send(Socket, "Album updated\n");
                                                {album_not_found, _} ->
                                                    gen_tcp:send(Socket, "Album not found\n")
                                            end;
                                        {album_not_found, _} ->
                                            gen_tcp:send(Socket, "Album not found\n")
                                    end
                            end
                    end,
                    userAuth(Socket,User);

                ["session_join", User] ->
                    session_manager ! {{session_join, User}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Session joined\n");
                        {user_exists, _} ->
                            gen_tcp:send(Socket, "User already in session\n")
                    end,
                    userAuth(Socket,User);

                ["session_leave", User] ->
                    session_manager ! {{session_leave, User}, self()},
                    receive
                        {ok, _} ->
                            gen_tcp:send(Socket, "Session left\n");
                        {user_not_found, _} ->
                            gen_tcp:send(Socket, "User not in session\n")
                    end,
                    userAuth(Socket,User);

                ["exit"] ->
                    gen_tcp:send(Socket, "Goodbye\n"),
                    gen_tcp:close(Socket);

                _ ->
                    gen_tcp:send(Socket, "Invalid command\n"),
                    userAuth(Socket,User)
            end
    end.



