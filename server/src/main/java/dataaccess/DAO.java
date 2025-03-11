package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class DAO {

    public DAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            // deal with error
        }
    }

    public void configureDB(String[] statements) {
        try {

            try (var connection = DatabaseManager.getConnection()) {
                for (var statement: statements) {
                    try (var preparedStatement = connection.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            // do something with error
        }
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> preparedStatement.setString(i + 1, p);
                        case Integer p -> preparedStatement.setInt(i + 1, p);
                        case null -> preparedStatement.setNull(i + 1, NULL);
                        default -> {
                            // default??
                        }
                    }
                }
                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: unable to update database");
        }
    }

    public ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                            default -> throw new DataAccessException("Unexpected value: " + param);
                    }
                }
                return ps.executeQuery();
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: unable to query database");
        }
    }
}
