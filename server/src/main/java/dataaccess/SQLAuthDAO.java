package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public String createAuth(String username) {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuth() {

    }
}
