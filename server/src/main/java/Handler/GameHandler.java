package Handler;

import Request.*;
import Result.CreateGameResult;
import Result.ListGameResult;
import dataaccess.DataAccessException;
import model.AuthData;
import spark.Request;
import spark.Response;

public class GameHandler extends Handler {

    public String createGame(Request request, Response response) {
        try {
            checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(e.getMessage());
        }
        try {
            CreateGameRequest gameRequest = createObj(request, CreateGameRequest.class);
            CreateGameResult newGameResult = gameService.createGame(gameRequest);
            return toJson(newGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return toJson(e.getMessage());
        }
    }

    public String listGames(Request request, Response response) {
        try {
            checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(e.getMessage());
        }
        try {
            ListGameResult listGameResult = gameService.listGames();
            return toJson(listGameResult);
        } catch (DataAccessException e) {
            response.status(500);
            return toJson(e.getMessage());
        }
    }

    public String joinGame(Request request, Response response) {
        AuthData authData;
        try {
            authData = checkAuth(request);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(e.getMessage());
        }
        try {
            JoinGameRequest joinGameRequest = createObj(request, JoinGameRequest.class);
            gameService.joinGame(joinGameRequest, authData);
            return "{}";
        } catch (DataAccessException e) {
            response.status(403);
            return toJson(e.getMessage());
        }
    }
}
