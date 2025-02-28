package Handler;

import Request.*;
import Result.CreateGameResult;
import Result.ErrorResult;
import Result.ListGameResult;
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
            CreateGameResult newGameResult = gameService.createGame(gameRequest);
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
            ListGameResult listGameResult = gameService.listGames();
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
                gameService.joinGame(joinGameRequest, authData);
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
