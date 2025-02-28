package Handler;

import Request.CreateGameRequest;
import Result.CreateGameResult;
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
}
