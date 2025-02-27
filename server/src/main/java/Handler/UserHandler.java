package Handler;

import Result.*;
import Request.*;
import dataaccess.DataAccessException;
import spark.*;

public class UserHandler extends Handler {
    // private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

//    public String register(Request request, Response response) {
//        RegisterRequest registerRequest = createObj(request, RegisterRequest.class);
//        // RegisterResult registerResult = userService.register(registerRequest);
//    }

    public String login(Request request, Response response) throws DataAccessException {
        // create LoginRequest object with username and password
        LoginRequest loginRequest = createObj(request, LoginRequest.class);
        // perform login request
        LoginResult loginResult = userService.login(loginRequest);
        var responseJson = toJson(loginResult);
        // response.body(responseJson);
        response.status(200);

        return responseJson;
    }
}
