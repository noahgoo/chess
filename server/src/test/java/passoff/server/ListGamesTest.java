package passoff.server;

import Request.CreateGameRequest;
import Result.GameInfo;
import Result.ListGameResult;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;

import java.util.ArrayList;
import java.util.List;

public class ListGamesTest {

    @Test
    public void listGamesSuccess() {
        var gameService = new GameService();
        try {
            gameService.createGame(new CreateGameRequest("testGame"));
            List<GameInfo> gameInfoList = new ArrayList<>();
            gameInfoList.add(new GameInfo(1, "","", "testGame"));
            var expectedGameInfo = new ListGameResult(gameInfoList);
            var actualGameInfo = gameService.listGames();
            Assertions.assertEquals(expectedGameInfo, actualGameInfo);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

    }
}
