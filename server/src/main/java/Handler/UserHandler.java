package Handler;

import Result.*;
import Request.*;
import dataaccess.DataAccessException;
import model.AuthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.*;

import java.util.Set;

public class UserHandler extends Handler {
    // private static final Logger log = LoggerFactory.getLogger(UserHandler.class);

    public String register(Request request, Response response) {
        try {
            RegisterRequest registerRequest = createObj(request, RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            response.status(200);
            return toJson(registerResult);
        } catch (DataAccessException e) {
            response.status(403);
            return toJson(e.getMessage());
        }

    }

    public String login(Request request, Response response) {
        try {
            // create LoginRequest object with username and password
            LoginRequest loginRequest = createObj(request, LoginRequest.class);
            // perform login request
            LoginResult loginResult = userService.login(loginRequest);
            var responseJson = toJson(loginResult);
            // response.body(responseJson);
            response.status(200);
            return responseJson;
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(e.getMessage());
        }
    }

    public String logout(Request request, Response response) {
        try {
            // check authToken
            String authToken = request.headers("authorization");
            AuthData authData = checkAuth(authToken);
            userService.logout(authData);
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(e.getMessage());
        }

    }
}
