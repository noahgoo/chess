package dataaccess;

import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;


public class SQLAuthDAO extends DAO implements AuthDAO {

    private final String[] authStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            );
            """
    };

    public SQLAuthDAO() {
        configureDB(authStatements);
    }



    @Override
    public String createAuth(String username) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        String token = UUID.randomUUID().toString();
        executeUpdate(statement, token, username);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        String statement = "SELECT username FROM auth WHERE authToken=?";
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken, rs.getString("username"));
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            return null;
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                var numberDeleted = ps.executeUpdate();
                if (numberDeleted==0) {
                    throw new DataAccessException("Error: authToken doesn't exist");
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clearAuth() throws DataAccessException {
        String statement = "TRUNCATE auth";
        executeUpdate(statement);
    }
}
