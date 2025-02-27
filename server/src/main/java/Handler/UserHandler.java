package Handler;

import Result.*;
import Request.*;
import dataaccess.DataAccessException;
import spark.*;

public class UserHandler extends Handler {
    // private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

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
}
