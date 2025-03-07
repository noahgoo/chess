package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.List;

public class SQLGameDAO extends DAO implements GameDAO {

    private final String[] gameStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game JSON NOT NULL,
                PRIMARY KEY (gameID)
            );
            """
    };

    public SQLGameDAO() {
        configureDB(gameStatements);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void clearGame() {

    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException {

    }
}
