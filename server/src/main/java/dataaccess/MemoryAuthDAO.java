package dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final static List<String> authMap = new ArrayList<>();

    @Override
    public String createAuth() {
        String token = UUID.randomUUID().toString();
        authMap.add(token);
        return token;
    }

    public void clearAuth() {
        authMap.clear();
    }
}
