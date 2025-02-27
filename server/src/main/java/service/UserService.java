package service;

import Request.*;
import Result.LoginResult;
import dataaccess.DataAccessException;
import model.UserData;

public class UserService extends Service {


    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        var userData = new UserData(loginRequest.username(), loginRequest.password(), null);

        // check for user
        try {
            userDao.getUser(userData);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        // create new Auth
        String auth = authDao.createAuth(userData.username());

        return new LoginResult(userData.username(), auth);
    }
}
