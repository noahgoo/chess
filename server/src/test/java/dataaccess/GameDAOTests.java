package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GameDAOTests {
    SQLUserDAO SQL_DAO = new SQLUserDAO();

    @Test
    public void clearUser() throws DataAccessException {
        SQL_DAO.createUser(new UserData("testUser", "testPass", null));
        SQL_DAO.clearUser();
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT * FROM user")) {
                var rs = ps.executeQuery();
                Assertions.assertFalse(rs.next());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("SQL error");
        }
    }

    @Test
    public void createUserFail() throws DataAccessException {
        SQL_DAO.clearUser();
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () ->
                SQL_DAO.createUser(new UserData(null, "testPass", null)));
        Assertions.assertEquals("Error: unable to update database", e.getMessage());
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        SQL_DAO.clearUser();
        SQL_DAO.createUser(new UserData("testUser", "testPass", "testEmail"));
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("SELECT username FROM user WHERE email=?")) {
                ps.setString(1, "testEmail");
                var rs = ps.executeQuery();
                if (rs.next()) {
                    String expected = "testUser";
                    String actual = rs.getString("username");
                    Assertions.assertEquals(expected, actual);
                } else { throw new DataAccessException("Error: didn't add user"); }

            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Test
    public void getUserFail() throws DataAccessException {
        SQL_DAO.clearUser();
        var actual = SQL_DAO.getUser(new UserData("testUser", "testPass", "testEmail"));
        Assertions.assertNull(actual);
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        SQL_DAO.clearUser();
        UserData expected = new UserData("testUser", "testPass", "testEmail");
        SQL_DAO.createUser(expected);
        UserData actual = SQL_DAO.getUser(new UserData("testUser", "testPass", null));
        Assertions.assertEquals(expected, actual);
    }
 }
