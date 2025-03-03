package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final static HashMap<String, UserData> USER_MAP = new HashMap<>();

    @Override
    public UserData getUser(UserData user) {

        if ((USER_MAP.containsKey(user.username()))&&(USER_MAP.get(user.username()).password().equals(user.password()))) {
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void createUser(UserData user) {
        USER_MAP.put(user.username(), user);
    }

    public void clearUser() {
        USER_MAP.clear();
    }
}
