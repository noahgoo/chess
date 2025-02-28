package handler;

import result.*;
import request.*;
import dataaccess.DataAccessException;
import model.AuthData;
import spark.*;

public class UserHandler extends Handler {

    public String register(Request request, Response response) {
        try {
            RegisterRequest registerRequest = createObj(request, RegisterRequest.class);
            if (registerRequest.password()==null) {
                response.status(400);
                return toJson(new ErrorResult("Error: bad request"));
            }
            RegisterResult registerResult = userService.register(registerRequest);
            response.status(200);
            return toJson(registerResult);
        } catch (DataAccessException e) {
            response.status(403);
            return toJson(new ErrorResult(e.getMessage()));
        }

    }

    public String login(Request request, Response response) {
        try {
            // create LoginRequest object with username and password
            LoginRequest loginRequest = createObj(request, LoginRequest.class);
            // perform login request
            LoginResult loginResult = userService.login(loginRequest);
            // response.body(responseJson);
            response.status(200);
            return toJson(loginResult);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }
    }

    public String logout(Request request, Response response) {
        try {
            // check authToken
            AuthData authData = checkAuth(request);
            userService.logout(authData);
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }

    }
}
