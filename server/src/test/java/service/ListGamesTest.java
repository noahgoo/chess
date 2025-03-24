package service;

import chess.ChessGame;
import model.GameData;
import request.CreateGameRequest;
import result.ListGameResult;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListGamesTest {

    @Test
    public void listGamesEmpty() throws DataAccessException {
        var gameService = new GameService();
        Service.GAME_DAO.clearGame();
        var gameList = new ArrayList<GameData>();
        ListGameResult actualResult = gameService.listGames();
        ListGameResult expectedResult = new ListGameResult(gameList);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void listGamesSuccess() {
        var gameService = new GameService();
        try {
            gameService.createGame(new CreateGameRequest("testGame"));
            List<GameData> gameInfoList = new ArrayList<>();
            gameInfoList.add(new GameData(1, null,null, "testGame", new ChessGame()));
            var expectedGameInfo = new ListGameResult(gameInfoList);
            var actualGameInfo = gameService.listGames();
            assertEquals(expectedGameInfo, actualGameInfo);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}
