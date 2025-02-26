package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final static List<GameData> gameMap = new ArrayList<>();

    @Override
    public void clearGame() {
        gameMap.clear();
    }
}
