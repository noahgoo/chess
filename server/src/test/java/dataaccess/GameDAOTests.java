package dataaccess;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GameDAOTests {
    SQLGameDAO sqlGameDAO = new SQLGameDAO();

    @Test
    public void clearGame() throws DataAccessException {
        sqlGameDAO.clearGame();
        sqlGameDAO.clearGame();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM game")) {
                var rs = ps.executeQuery();
                Assertions.assertFalse(rs.next());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }
    }

    @Test
    public void createGameFail() throws DataAccessException {
        sqlGameDAO.clearGame();
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () -> sqlGameDAO.createGame(null));
        Assertions.assertEquals("Error: unable to update database", e.getMessage());
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        sqlGameDAO.clearGame();
        int actualGameID = sqlGameDAO.createGame("testGame");
        Assertions.assertEquals(1, actualGameID);
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        sqlGameDAO.clearGame();
        var actualList = sqlGameDAO.listGames();
        Assertions.assertEquals(0, actualList.toArray().length);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        sqlGameDAO.clearGame();
        sqlGameDAO.createGame("testGame");
        var actualList = sqlGameDAO.listGames();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM game")) {
                var rs = ps.executeQuery();
                if (rs.next()) {
                    Assertions.assertEquals(rs.getString("gameName"), actualList.getFirst().gameName());
                    Assertions.assertEquals(1, actualList.toArray().length);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Couldn't query");
        }
    }

    @Test
    public void getGameFail() throws DataAccessException {
        sqlGameDAO.clearGame();
        sqlGameDAO.createGame("testGame");
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () -> sqlGameDAO.getGame(2));
        Assertions.assertEquals("Error: game doesn't exist", e.getMessage());
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        sqlGameDAO.clearGame();
        int id = sqlGameDAO.createGame("testGame");
        GameData actualGame = sqlGameDAO.getGame(id);
        Assertions.assertEquals("testGame", actualGame.gameName());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        sqlGameDAO.clearGame();
        int id = sqlGameDAO.createGame("testGame");
        var game = sqlGameDAO.getGame(id);
        sqlGameDAO.updateGame("WHITE", game, new AuthData("temp", "cougar"));
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () ->
                sqlGameDAO.updateGame("WHITE", game, new AuthData("temp", "cougar")));
        Assertions.assertEquals("Error: already taken", e.getMessage());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        sqlGameDAO.clearGame();
        int id = sqlGameDAO.createGame("testGame");
        var game = sqlGameDAO.getGame(id);
        sqlGameDAO.updateGame("WHITE", game, new AuthData("temp", "cougar"));
        var actualGame = sqlGameDAO.getGame(id);
        Assertions.assertEquals("cougar", actualGame.whiteUsername());
    }
}
