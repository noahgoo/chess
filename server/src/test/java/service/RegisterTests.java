package service;

import request.RegisterRequest;
import result.RegisterResult;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterTests {

    @Test
    public void registerFail() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword", "testEmail");
        UserService userService = new UserService();
        Service.USER_DAO.createUser(new UserData("testUser", "testPassword", "testEmail"));
        DataAccessException e = assertThrows(DataAccessException.class, () -> userService.register(registerRequest));

        assertEquals("Error: already taken", e.getMessage());
    }

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPassword", "testEmail");
        UserService userService = new UserService();
        RegisterResult actualRegisterResult = userService.register(registerRequest);
        RegisterResult expectedRegisterResult = new RegisterResult("testUser", actualRegisterResult.authToken());

        assertEquals(actualRegisterResult, expectedRegisterResult);
    }

}
