package dataaccess;

public interface AuthDAO {

    public String createAuth(String username);

    public void clearAuth();
}
