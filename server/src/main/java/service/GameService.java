package service;

import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.GameInfo;
import result.ListGameResult;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService extends Service {

    public CreateGameResult createGame(CreateGameRequest gameRequest) throws DataAccessException {
        int gameID = GAME_DAO.createGame(gameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGameResult listGames() throws DataAccessException {
        List<GameData> gamesList = GAME_DAO.listGames();
        List<GameInfo> gameInfoList = new ArrayList<>();
        for (GameData gameInfo: gamesList) {
            gameInfoList.add(new GameInfo(gameInfo.gameID(), gameInfo.whiteUsername(), gameInfo.blackUsername(), gameInfo.gameName()));
        }
        return new ListGameResult(gameInfoList);
    }

    public void joinGame(JoinGameRequest joinGameRequest, AuthData authData) throws DataAccessException {
        GameData currentGame = GAME_DAO.getGame(joinGameRequest.gameID());
        GAME_DAO.updateGame(joinGameRequest.playerColor(), currentGame, authData);
    }
}
