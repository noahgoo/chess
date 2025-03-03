package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final static HashMap<String, GameData> GAME_MAP = new HashMap<>();

    @Override
    public void clearGame() {
        GAME_MAP.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName!=null) {
            int gameID = GAME_MAP.size() + 1;
            GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
            GAME_MAP.put(String.valueOf(gameID), newGame);
            return gameID;
        } else {
            throw new DataAccessException("Error: no game name");
        }
    }

    @Override
    public List<GameData> listGames() {
        List<GameData> gamesList = new ArrayList<>();
        for (Map.Entry<String, GameData> game: GAME_MAP.entrySet()) {
            gamesList.add(game.getValue());
        }
        return gamesList;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (GAME_MAP.containsKey(String.valueOf(gameID))) {
            return GAME_MAP.get(String.valueOf(gameID));
        } else { throw new DataAccessException("Error: game doesn't exist"); }
    }

    @Override
    public void updateGame(String playerColor, GameData game, AuthData authData) throws DataAccessException{
        GameData currentGame = GAME_MAP.get(String.valueOf(game.gameID()));
        if (playerColor.equals("WHITE")&&game.whiteUsername()==null) {
            GameData updatedGame = new GameData(currentGame.gameID(), authData.username(),
                    currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            GAME_MAP.remove(String.valueOf(game.gameID()));
            GAME_MAP.put(String.valueOf(game.gameID()), updatedGame);
        } else if (playerColor.equals("BLACK")&&game.blackUsername()==null) {
            GameData updatedGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(),
                    authData.username(), currentGame.gameName(), currentGame.game());
            GAME_MAP.remove(String.valueOf(game.gameID()));
            GAME_MAP.put(String.valueOf(game.gameID()), updatedGame);
        } else {
            throw new DataAccessException("Error: already taken");
        }

    }
}
