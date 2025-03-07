package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public String createAuth(String username) throws DataAccessException;
    public AuthData getAuth(String authToken);
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clearAuth() throws DataAccessException;
}
