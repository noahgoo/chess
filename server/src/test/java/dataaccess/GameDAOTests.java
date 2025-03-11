package dataaccess;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GameDAOTests {
    SQLGameDAO SQL_DAO = new SQLGameDAO();

    @Test
    public void clearGame() throws DataAccessException {
        SQL_DAO.clearGame();
        SQL_DAO.clearGame();
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
        SQL_DAO.clearGame();
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () -> SQL_DAO.createGame(null));
        Assertions.assertEquals("Error: unable to update database", e.getMessage());
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        SQL_DAO.clearGame();
        int actualGameID = SQL_DAO.createGame("testGame");
        Assertions.assertEquals(1, actualGameID);
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        SQL_DAO.clearGame();
        var actualList = SQL_DAO.listGames();
        Assertions.assertEquals(0, actualList.toArray().length);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        SQL_DAO.clearGame();
        SQL_DAO.createGame("testGame");
        var actualList = SQL_DAO.listGames();
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
        SQL_DAO.clearGame();
        SQL_DAO.createGame("testGame");
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () -> SQL_DAO.getGame(2));
        Assertions.assertEquals("Error: game doesn't exist", e.getMessage());
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        SQL_DAO.clearGame();
        int id = SQL_DAO.createGame("testGame");
        GameData actualGame = SQL_DAO.getGame(id);
        Assertions.assertEquals("testGame", actualGame.gameName());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        SQL_DAO.clearGame();
        int id = SQL_DAO.createGame("testGame");
        var game = SQL_DAO.getGame(id);
        SQL_DAO.updateGame("WHITE", game, new AuthData("temp", "cougar"));
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () ->
                SQL_DAO.updateGame("WHITE", game, new AuthData("temp", "cougar")));
        Assertions.assertEquals("Error: already taken", e.getMessage());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        SQL_DAO.clearGame();
        int id = SQL_DAO.createGame("testGame");
        var game = SQL_DAO.getGame(id);
        SQL_DAO.updateGame("WHITE", game, new AuthData("temp", "cougar"));
        var actualGame = SQL_DAO.getGame(id);
        Assertions.assertEquals("cougar", actualGame.whiteUsername());
    }
}
