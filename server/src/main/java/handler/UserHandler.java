package handler;

import result.*;
import request.*;
import dataaccess.DataAccessException;
import model.AuthData;
import spark.*;

public class UserHandler extends Handler {

    public String register(Request request, Response response) {
        try {
            // makes sure there is a valid password as part of the RegisterRequest
            RegisterRequest registerRequest = createObj(request, RegisterRequest.class);
            if (registerRequest.password()==null) {
                response.status(400);
                return toJson(new ErrorResult("Error: bad request"));
            }

            RegisterResult registerResult = USER_SERVICE.register(registerRequest);
            response.status(200);
            return toJson(registerResult);
        } catch (DataAccessException e) {
            response.status(403);
            return toJson(new ErrorResult(e.getMessage()));
        }

    }

    public String login(Request request, Response response) {
        try {
            LoginRequest loginRequest = createObj(request, LoginRequest.class);
            LoginResult loginResult = USER_SERVICE.login(loginRequest);
            response.status(200);
            return toJson(loginResult);
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }
    }

    public String logout(Request request, Response response) {
        try {
            AuthData authData = checkAuth(request);
            USER_SERVICE.logout(authData);
            response.status(200);
            return "{}";
        } catch (DataAccessException e) {
            response.status(401);
            return toJson(new ErrorResult(e.getMessage()));
        }

    }
}
