package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.List;

public interface GameDAO {
    public int createGame(String gameName) throws DataAccessException;
    public void clearGame();
    public List<GameData> listGames();
    public GameData getGame(int gameID) throws DataAccessException;
    public void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException;
}
