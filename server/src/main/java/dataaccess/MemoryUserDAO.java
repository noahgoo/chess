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
    public UserData getUser(UserData user) {

        if ((userMap.containsKey(user.username()))&&(userMap.get(user.username()).password().equals(user.password()))) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void createUser(UserData user) {
        userMap.put(user.username(), user);
    }

    public void clearUser() {
        userMap.clear();
    }
}
