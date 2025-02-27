package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataaccess.DataAccessException;

public class MemoryUserDAO implements UserDAO {
    private final static HashMap<String, UserData> userMap = new HashMap();

    @Override
    public UserData getUser(UserData user) throws DataAccessException {
        userMap.put("testUser", new UserData("testUser", "testPassword", null));

        if ((userMap.containsKey(user.username()))&&(userMap.get(user.username()).password().equals(user.password()))) {
            return user;
        } else {
            throw new DataAccessException("Error: no known user");
        }
    }

    public void clearUser() {
        userMap.clear();
    }
}
