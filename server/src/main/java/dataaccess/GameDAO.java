package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    void clearGame() throws DataAccessException;
    List<GameData> listGames();
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException;
}
