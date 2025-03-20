package net;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.io.IOException;

// one method for every request, take in LoginRequest and return LoginResult
// has the 7 methods
public class ServerFacade {
    private static final ClientCommunicator CLIENT = new ClientCommunicator();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        var path = serverUrl + "/user";
        return CLIENT.doPost(path, registerRequest, RegisterResult.class);

    }
    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = serverUrl + "/session";
        return CLIENT.doPost(path, loginRequest, LoginResult.class);
    }
}
