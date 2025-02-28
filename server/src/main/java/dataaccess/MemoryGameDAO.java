package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<GameData> listGames() {
        List<GameData> gamesList = new ArrayList<>();
        for (Map.Entry<String, GameData> game: gameMap.entrySet()) {
            gamesList.add(game.getValue());
        }
        return gamesList;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (gameMap.containsKey(String.valueOf(gameID))) {
            return gameMap.get(String.valueOf(gameID));
        } else { throw new DataAccessException("Error: game doesn't exist"); }
    }

    @Override
    public void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException{
        GameData currentGame = gameMap.get(String.valueOf(game.gameID()));
        if (playerColor.equals("WHITE")&&game.whiteUsername()==null) {
            GameData updatedGame = new GameData(currentGame.gameID(), authData.username(), currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            gameMap.remove(String.valueOf(game.gameID()));
            gameMap.put(String.valueOf(game.gameID()), updatedGame);
        } else if (playerColor.equals("BLACK")&&game.blackUsername()==null) {
            GameData updatedGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(), authData.username(), currentGame.gameName(), currentGame.game());
            gameMap.remove(String.valueOf(game.gameID()));
            gameMap.put(String.valueOf(game.gameID()), updatedGame);
        } else {
            throw new DataAccessException("Error: already taken");
        }

    }
}
