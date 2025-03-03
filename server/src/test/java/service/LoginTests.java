package service;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginTests {

    @Test
    public void loginFail() {
        // var user = new UserData("testUser", "testPassword", null);

        var loginRequest = new LoginRequest("testUser1", "testPassword");
        var loginService = new UserService();
        DataAccessException e = assertThrows(DataAccessException.class, () -> loginService.login(loginRequest));

        assertEquals("Error: unauthorized", e.getMessage());

        }

    @Test
    public void loginSuccess() throws DataAccessException {
        var loginRequest = new LoginRequest("testUser", "testPassword");
        var loginService = new UserService();
        Service.USER_DAO.clearUser();
        loginService.register(new RegisterRequest("testUser", "testPassword", "testEmail"));

        var actualLoginResponse = loginService.login(loginRequest);
        var expectedLoginResponse = new LoginResult("testUser", actualLoginResponse.authToken());

        assertEquals(actualLoginResponse, expectedLoginResponse);

    }
}
