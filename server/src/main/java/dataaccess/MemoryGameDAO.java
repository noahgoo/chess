package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final static HashMap<String, GameData> gameMap = new HashMap<>();

    @Override
    public void clearGame() {
        gameMap.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName!=null) {
            int gameID = gameMap.size() + 1;
            GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
            gameMap.put(String.valueOf(gameID), newGame);
            return gameID;
        } else {
            throw new DataAccessException("Error: no game name");
        }
    }

}
