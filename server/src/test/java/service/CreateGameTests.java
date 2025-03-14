package service;

import request.CreateGameRequest;
import result.CreateGameResult;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameTests {

    @Test
    public void createGameFail() {
        var gameRequest = new CreateGameRequest(null);
        var gameService = new GameService();
        DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.createGame(gameRequest));

        assertEquals("Error: no game name", e.getMessage());
    }

    @Test
    public void createGameSuccess() {
        try {
            var gameRequest = new CreateGameRequest("gameTest");
            var gameService = new GameService();
            var gameResponse = gameService.createGame(gameRequest);
            assertEquals(new CreateGameResult(1), gameResponse);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }


    }
}
