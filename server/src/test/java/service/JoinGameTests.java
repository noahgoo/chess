package service;

import request.CreateGameRequest;
import request.JoinGameRequest;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinGameTests {

    @Test
    public void joinGameFail() {
        var gameService = new GameService();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        AuthData authData = new AuthData("temp", "testUser");
        DataAccessException e = assertThrows(DataAccessException.class, () -> gameService.joinGame(joinGameRequest, authData));

        assertEquals("Error: game doesn't exist", e.getMessage());
    }

    @Test
    public void joinGameSuccess() {
        var gameService = new GameService();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);
        AuthData authData = new AuthData("temp", "testUser");
        try {
            var gameResult = gameService.createGame(new CreateGameRequest("testGame"));
            GameData newGame = gameService.gameDao.getGame(gameResult.gameID());
            gameService.joinGame(joinGameRequest, authData);
            GameData expectedGame = new GameData(1, "testUser", null, "testGame", newGame.game());
            GameData actualGame = gameService.gameDao.getGame(1);
            assertEquals(expectedGame, actualGame);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }


    }
}
