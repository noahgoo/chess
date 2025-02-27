package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final static HashMap<String, String> authMap = new HashMap<>();

    @Override
    public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        authMap.put(username, token);
        return token;
    }

    public void clearAuth() {
        authMap.clear();
    }
}
