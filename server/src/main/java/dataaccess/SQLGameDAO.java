package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.List;

public class SQLGameDAO implements GameDAO {
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
