package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class UserDAOTests {
    SQLUserDAO sqlUserDAO = new SQLUserDAO();

    @Test
    public void clearUser() throws DataAccessException {
        sqlUserDAO.createUser(new UserData("testUser", "testPass", null));
        sqlUserDAO.clearUser();
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
        sqlUserDAO.clearUser();
        DataAccessException e = Assertions.assertThrows(DataAccessException.class, () ->
                sqlUserDAO.createUser(new UserData(null, "testPass", null)));
        Assertions.assertEquals("Error: unable to update database", e.getMessage());
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        sqlUserDAO.clearUser();
        sqlUserDAO.createUser(new UserData("testUser", "testPass", "testEmail"));
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
        sqlUserDAO.clearUser();
        var actual = sqlUserDAO.getUser(new UserData("testUser", "testPass", "testEmail"));
        Assertions.assertNull(actual);
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        sqlUserDAO.clearUser();
        UserData user = new UserData("testUser", "testPass", "testEmail");
        sqlUserDAO.createUser(user);
        UserData actual = sqlUserDAO.getUser(new UserData("testUser", "testPass", null));
        UserData expected = new UserData("testUser", actual.password(), "testEmail");
        Assertions.assertEquals(expected, actual);
    }
 }
