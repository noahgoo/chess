package service;

import request.*;
import result.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService extends Service {

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        if (USER_DAO.getUser(userData)==null) {
            USER_DAO.createUser(userData);
            String token = AUTH_DAO.createAuth(userData.username());
            return new RegisterResult(userData.username(), token);
        } else {
            throw new DataAccessException("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), null);

        if (USER_DAO.getUser(userData)==null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // create new Auth
        String auth = AUTH_DAO.createAuth(userData.username());
        return new LoginResult(userData.username(), auth);
    }

    public void logout(AuthData authData) throws DataAccessException {
        try {
            Service.AUTH_DAO.deleteAuth(authData.authToken());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
