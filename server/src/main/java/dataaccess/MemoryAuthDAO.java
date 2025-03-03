package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final static HashMap<String, AuthData> AUTH_MAP = new HashMap<>();

    @Override
    public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        AUTH_MAP.put(token, authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return AUTH_MAP.getOrDefault(authToken, null);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (AUTH_MAP.containsKey(authToken)) {
            AUTH_MAP.remove(authToken);
        } else {
            throw new DataAccessException("Error: authToken doesn't exist");
        }
    }

    public void clearAuth() {
        AUTH_MAP.clear();
    }
}
