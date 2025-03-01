package service;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogoutTests {

    @Test
    public void logoutFail() {
        var userService = new UserService();
        Service.authDao.createAuth("testUser");
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.logout(new AuthData("wrongAuth", "testUser")));

        assertEquals("Error: authToken doesn't exist", e.getMessage());
    }

    @Test
    public void logoutSuccess() {
        var userService = new UserService();
        String auth = Service.authDao.createAuth("testUser");
        try {
            userService.logout(new AuthData(auth, "testUser"));
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        Assertions.assertNull(Service.authDao.getAuth(auth));
    }
}
