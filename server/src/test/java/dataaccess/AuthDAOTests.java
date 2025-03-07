package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthDAOTests {
    SQLAuthDAO SQL_DAO = new SQLAuthDAO();

    @Test
    public void getAuthFail() {
        SQL_DAO.clearAuth();
        SQL_DAO.createAuth("getAuthUser");
        AuthData actual = SQL_DAO.getAuth("wrong auth");
        Assertions.assertNull(actual);
    }

    @Test
    public void getAuthSuccess() {
        SQL_DAO.clearAuth();
        String authToken = SQL_DAO.createAuth("getAuthUser");
        AuthData actual = SQL_DAO.getAuth(authToken);
        AuthData expected = new AuthData(authToken, "getAuthUser");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteAuthFail() {
        SQL_DAO.clearAuth();
        SQL_DAO.createAuth("deleteAuthUser");
        DataAccessException e = assertThrows(DataAccessException.class, () -> SQL_DAO.deleteAuth("wrong auth"));
        Assertions.assertEquals("Error: authToken doesn't exist", e.getMessage());
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        SQL_DAO.clearAuth();
        String authToken = SQL_DAO.createAuth("deleteAuthUser");
        SQL_DAO.deleteAuth(authToken);
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT authToken FROM auth WHERE username=?")) {
                ps.setString(1, "deleteAuthUser");
                var rs = ps.executeQuery();
                Assertions.assertFalse(rs.next());
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: bad connection");
        }
    }

    @Test
    public void createAuthFail() {
        SQL_DAO.clearAuth();
        String authToken1 = SQL_DAO.createAuth("createAuthUser");
        String authToken2 = SQL_DAO.createAuth("createAuthUser");
        Assertions.assertNotEquals(authToken1, authToken2);
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        String authToken = SQL_DAO.createAuth("testUser");
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT username FROM auth WHERE authToken=?")) {
                ps.setString(1, authToken);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    String expected = "testUser";
                    String actual = rs.getString("username");
                    Assertions.assertEquals(expected, actual);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }
    }

    @Test
    public void clearAuth() throws DataAccessException {
        SQL_DAO.createAuth("testClearUser");
        SQL_DAO.clearAuth();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM auth")) {
                var rs = ps.executeQuery();
                    Assertions.assertFalse(rs.next());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }
    }
}
