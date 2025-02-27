package service;

import Request.*;
import Result.*;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService extends Service {

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        if (userDao.getUser(userData)==null) {
            userDao.createUser(userData);
            String token = authDao.createAuth(userData.username());
            RegisterResult registerResult = new RegisterResult(userData.username(), token);
            return registerResult;
        } else {
            throw new DataAccessException("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), null);

        if (userDao.getUser(userData)==null) {
            throw new DataAccessException("Error: unauthorized");
        }
        // create new Auth
        String auth = authDao.createAuth(userData.username());
        return new LoginResult(userData.username(), auth);
    }
}
