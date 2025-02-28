package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final static HashMap<String, AuthData> authMap = new HashMap<>();

    @Override
    public String createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authMap.put(token, authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authMap.getOrDefault(authToken, null);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authMap.containsKey(authToken)) {
            authMap.remove(authToken);
        } else {
            throw new DataAccessException("Error: authToken doesn't exist");
        }
    }

    public void clearAuth() {
        authMap.clear();
    }
}
