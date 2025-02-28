package dataaccess;

import Result.CreateGameResult;

public interface GameDAO {
    public int createGame(String gameName) throws DataAccessException;
    public void clearGame();
}
