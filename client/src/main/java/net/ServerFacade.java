package net;

import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGameResult;
import result.LoginResult;
import result.RegisterResult;

public class ServerFacade {
    private static final HTTPCommunicator CLIENT = new HTTPCommunicator();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        var path = serverUrl + "/user";
        return CLIENT.doPost(path, registerRequest, RegisterResult.class, null);

    }
    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = serverUrl + "/session";
        return CLIENT.doPost(path, loginRequest, LoginResult.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        var path = serverUrl + "/session";
        CLIENT.doDelete(path, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {
        var path = serverUrl + "/game";
        return CLIENT.doPost(path, createGameRequest, CreateGameResult.class, authToken);
    }

    public ListGameResult listGames(String authToken) throws ResponseException {
        var path = serverUrl + "/game";
        return CLIENT.doGet(path, ListGameResult.class, authToken);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        var path = serverUrl + "/game";
        CLIENT.doPut(path, joinGameRequest, authToken);
    }

    public void clear(String authToken) throws ResponseException {
        var path = serverUrl + "/db";
        CLIENT.doDelete(path, authToken);
    }
}
