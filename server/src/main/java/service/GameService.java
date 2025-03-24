package service;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGameResult;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService extends Service {

    public CreateGameResult createGame(CreateGameRequest gameRequest) throws DataAccessException {
        int gameID = GAME_DAO.createGame(gameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGameResult listGames() throws DataAccessException {
        List<GameData> gamesList = GAME_DAO.listGames();
        return new ListGameResult(gamesList);
    }

    public void joinGame(JoinGameRequest joinGameRequest, AuthData authData) throws DataAccessException {
        GameData currentGame = GAME_DAO.getGame(joinGameRequest.gameID());
        GAME_DAO.updateGame(joinGameRequest.playerColor(), currentGame, authData);
    }
}
