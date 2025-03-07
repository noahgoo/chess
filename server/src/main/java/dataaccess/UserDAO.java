package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    public void createUser(UserData user) throws DataAccessException;
    public UserData getUser(UserData user) throws DataAccessException;
    public void clearUser() throws DataAccessException;
}
