package dataaccess;

import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.Types.NULL;

public class DAO {

    public void configureDB(String[] authStatements) {
        try {
            DatabaseManager.createDatabase();
            try (var connection = DatabaseManager.getConnection()) {
                for (var statement: authStatements) {
                    try (var preparedStatement = connection.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            // do something with error
        }
    }

    public int executeUpdate(String statement, Object... params) {
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

                var response = preparedStatement.getGeneratedKeys();
                if (response.next()) {
                    return response.getInt(1);

                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            // deal with error
            return 0;
        }
    }
}
