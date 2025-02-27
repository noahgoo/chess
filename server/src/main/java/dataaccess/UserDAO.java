package dataaccess;

import model.UserData;

public interface UserDAO {
    public UserData getUser(UserData user) throws DataAccessException;
    public void clearUser();
}
