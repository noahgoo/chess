package passoff.server;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutTests {

    @Test
    public void logoutFail() {
        var userService = new UserService();
        userService.authDao.createAuth("testUser");
        DataAccessException e = assertThrows(DataAccessException.class, () -> {
            userService.logout(new AuthData("wrongAuth", "testUser"));
        });

        assertEquals("Error: authToken doesn't exist", e.getMessage());
    }

    @Test
    public void logoutSuccess() {
        var userService = new UserService();
        String auth = userService.authDao.createAuth("testUser");
        try {
            userService.logout(new AuthData(auth, "testUser"));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(userService.authDao.getAuth(auth),null);
    }
}
