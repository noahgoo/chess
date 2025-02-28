package Result;

import java.util.Objects;

public record GameInfo(int gameID, String whiteUsername, String blackUsername, String gameName) {
    public GameInfo {
        whiteUsername = Objects.requireNonNullElse(whiteUsername, "");
        blackUsername = Objects.requireNonNullElse(blackUsername, "");
    }
}
