package service;

import request.CreateGameRequest;
import result.GameInfo;
import result.ListGameResult;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesTest {

    @Test
    public void listGamesFail() {
        var gameService = new GameService();
        Service.gameDao.clearGame();
        DataAccessException e = assertThrows(DataAccessException.class, gameService::listGames);

        assertEquals("Error: no current games", e.getMessage());
    }

    @Test
    public void listGamesSuccess() {
        var gameService = new GameService();
        try {
            gameService.createGame(new CreateGameRequest("testGame"));
            List<GameInfo> gameInfoList = new ArrayList<>();
            gameInfoList.add(new GameInfo(1, null,null, "testGame"));
            var expectedGameInfo = new ListGameResult(gameInfoList);
            var actualGameInfo = gameService.listGames();
            assertEquals(expectedGameInfo, actualGameInfo);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}
