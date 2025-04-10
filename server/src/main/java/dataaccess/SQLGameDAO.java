package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SQLGameDAO extends DAO implements GameDAO {

    public SQLGameDAO() {
        String[] gameStatements = {
                """
            CREATE TABLE IF NOT EXISTS game (
                id INT NOT NULL AUTO_INCREMENT,
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game JSON NOT NULL,
                PRIMARY KEY (id),
                UNIQUE (gameID)
            );
            """
        };
        configureDB(gameStatements);

    }

    public int getMaxGameID() throws DataAccessException {
        String statement = "SELECT MAX(gameID) FROM game";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);  // Get the maximum gameID from the result
            } else {
                return 0;  // If no games exist, return 0 (or start from 1)
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving the max gameID");
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        ChessGame newChessGame = new ChessGame();
        var jsonChess = new Gson().toJson(newChessGame);
        int gameID = getMaxGameID() + 1;
        executeUpdate(statement, gameID, null, null, gameName, jsonChess);
        return gameID;
    }

    @Override
    public void clearGame() throws DataAccessException {
        String statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public List<GameData> listGames() {
        String statement = "SELECT * FROM game";
        List<GameData> gameDataList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                GameData gameData = new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), chessGame);
                gameDataList.add(gameData);
            }
            return gameDataList;
        } catch (DataAccessException | SQLException e) {
            return List.of();
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var jsonGame = rs.getString("game");
                        ChessGame chessGame = new Gson().fromJson(jsonGame, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"), chessGame);
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: game doesn't exist");
        }
        throw new DataAccessException("Error: game doesn't exist");
    }

    @Override
    public void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException {
        String statement = "SELECT * FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
             ps.setInt(1, game.gameID());
             try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String updateStatement;
                    if (playerColor==null) {
                        updateStatement = "UPDATE game SET game=? WHERE gameID=?";
                        String newGame = new Gson().toJson(game.game());
                        executeUpdate(updateStatement, newGame, game.gameID());
                    }
                    else if (playerColor.equals("WHITE")&&rs.getString("whiteUsername")==null) {
                        updateStatement = "UPDATE game SET whiteUsername=? WHERE gameID=?";
                        executeUpdate(updateStatement, authData.username(), game.gameID());
                    } else if (playerColor.equals("BLACK")&&rs.getString("blackUsername")==null) {
                        updateStatement = "UPDATE game SET blackUsername=? WHERE gameID=?";
                        executeUpdate(updateStatement, authData.username(), game.gameID());
                    } else if (authData==null) {
                        updateStatement = "UPDATE game SET game=? WHERE gameID=?";
                        String newGame = new Gson().toJson(game.game());
                        executeUpdate(updateStatement, newGame, game.gameID());
                    } else if (playerColor.equals("WHITE")&&game.whiteUsername().equals(authData.username())) {
                        updateStatement = "UPDATE game SET whiteUsername=? WHERE gameID=?";
                        executeUpdate(updateStatement, null, game.gameID());
                    } else if (playerColor.equals("BLACK")&&game.blackUsername().equals(authData.username())) {
                        updateStatement = "UPDATE game SET blackUsername=? WHERE gameID=?";
                        executeUpdate(updateStatement, null, game.gameID());
                    } else { throw new DataAccessException("Error: already taken"); }
                }
             }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: already taken");
        }
    }
}
