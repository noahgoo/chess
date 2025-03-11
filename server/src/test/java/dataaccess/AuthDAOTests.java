package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthDAOTests {
    SQLAuthDAO sqlAuthDAO = new SQLAuthDAO();

    @Test
    public void getAuthFail() throws DataAccessException {
        sqlAuthDAO.clearAuth();
        sqlAuthDAO.createAuth("getAuthUser");
        AuthData actual = sqlAuthDAO.getAuth("wrong auth");
        Assertions.assertNull(actual);
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        sqlAuthDAO.clearAuth();
        String authToken = sqlAuthDAO.createAuth("getAuthUser");
        AuthData actual = sqlAuthDAO.getAuth(authToken);
        AuthData expected = new AuthData(authToken, "getAuthUser");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteAuthFail() throws DataAccessException {
        sqlAuthDAO.clearAuth();
        sqlAuthDAO.createAuth("deleteAuthUser");
        DataAccessException e = assertThrows(DataAccessException.class, () -> sqlAuthDAO.deleteAuth("wrong auth"));
        Assertions.assertEquals("Error: authToken doesn't exist", e.getMessage());
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        sqlAuthDAO.clearAuth();
        String authToken = sqlAuthDAO.createAuth("deleteAuthUser");
        sqlAuthDAO.deleteAuth(authToken);
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
    public void createAuthFail() throws DataAccessException {
        sqlAuthDAO.clearAuth();
        DataAccessException e = assertThrows(DataAccessException.class, () -> sqlAuthDAO.createAuth(null));
        Assertions.assertNotEquals("Error: unable to access database", e.getMessage());
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        String authToken = sqlAuthDAO.createAuth("testUser");
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
        sqlAuthDAO.createAuth("testClearUser");
        sqlAuthDAO.clearAuth();
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
