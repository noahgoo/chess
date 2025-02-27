package passoff.server;

import Handler.UserHandler;
import Request.LoginRequest;
import Result.LoginResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginTests {

    @Test
    public void readJSONBody() {
        var serializer = new Gson();
        var user = new UserData("testUser", "testPassword", "testEmail");
        var json = serializer.toJson(user);

        Map<String, String> jsonBody = new HashMap<>();
        jsonBody.put("username", "testUser");
        jsonBody.put("password", "testPassword");

        UserHandler loginTest = new UserHandler();
    }

    @Test
    public void loginFail() {
        // var user = new UserData("testUser", "testPassword", null);

        var loginRequest = new LoginRequest("testUser1", "testPassword");
        var loginService = new UserService();
        DataAccessException e = assertThrows(DataAccessException.class, () -> {
            loginService.login(loginRequest);
        });

        assertEquals("Error: no known user", e.getMessage());

        }

    @Test
    public void loginSuccess() throws DataAccessException {
        var loginRequest = new LoginRequest("testUser", "testPassword");
        var loginService = new UserService();

        var actualLoginResponse = loginService.login(loginRequest);
        var expectedLoginResponse = new LoginResult("testUser", actualLoginResponse.authToken());

        assertEquals(actualLoginResponse, expectedLoginResponse);

    }
}
