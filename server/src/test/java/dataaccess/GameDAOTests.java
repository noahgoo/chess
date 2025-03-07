package dataaccess;

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
}
