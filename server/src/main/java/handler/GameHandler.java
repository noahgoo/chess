package handler;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ErrorResult;
import result.ListGameResult;
import dataaccess.DataAccessException;
import model.AuthData;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class GameHandler extends Handler {

    public String createGame(Request request, Response response) {
        try {
            checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }
        try {
            CreateGameRequest gameRequest = createObj(request, CreateGameRequest.class);
            CreateGameResult newGameResult = GAME_SERVICE.createGame(gameRequest);
            return toJson(newGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return toJson(new ErrorResult(e.getMessage()));
        }
    }

    public String listGames(Request request, Response response) {
        try {
            checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }
        try {
            ListGameResult listGameResult = GAME_SERVICE.listGames();
            return toJson(listGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return toJson(new ErrorResult(e.getMessage()));
        }
    }

    public String joinGame(Request request, Response response) {
        AuthData authData;
        try {
            authData = checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }
        try {
            JoinGameRequest joinGameRequest = createObj(request, JoinGameRequest.class);
            if ((joinGameRequest.playerColor()!=null&&joinGameRequest.gameID()!=0)&&
                    Objects.equals(joinGameRequest.playerColor(), "WHITE") || Objects.equals(joinGameRequest.playerColor(), "BLACK")) {
                GAME_SERVICE.joinGame(joinGameRequest, authData);
                return "{}";
            } else {
                response.status(400);
                return toJson(new ErrorResult("Error: bad request"));
            }
        } catch (DataAccessException e) {
            response.status(403);
            return toJson(new ErrorResult(e.getMessage()));
        }
    }
}
