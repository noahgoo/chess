package dataaccess;

import java.util.ArrayList;
import java.util.List;

public class MemoryUserDAO implements UserDAO {
    private final static List<String> userMap = new ArrayList<>();

    @Override
    public String getUser(String request) {
        if (userMap.contains(request)) {
            return request;
        } else {
            return "Error: not found";
        }
    }

    public void clearUser() {
        userMap.clear();
    }
}
