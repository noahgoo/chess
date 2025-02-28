package service;

import Request.CreateGameRequest;
import Result.CreateGameResult;
import dataaccess.DataAccessException;
import model.GameData;

public class GameService extends Service {

    public CreateGameResult createGame(CreateGameRequest gameRequest) throws DataAccessException {
        int gameID = gameDao.createGame(gameRequest.gameName());
        return new CreateGameResult(gameID);
    }
}
