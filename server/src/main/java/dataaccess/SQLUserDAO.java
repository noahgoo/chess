package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO extends DAO implements UserDAO {

    private final String[] userStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) 
            );
            """
    };

    public SQLUserDAO() {
        configureDB(userStatements);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(UserData user) throws DataAccessException {
        String statement = "SELECT username, password, email FROM user WHERE username=? AND password=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"),
                            rs.getString("email"));
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearUser() throws DataAccessException {
        String statement = "TRUNCATE user";
        executeUpdate(statement);
    }
}
