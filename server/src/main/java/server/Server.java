package server;

import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import handler.WebSocketHandler;
import spark.*;

public class Server {
    private static final UserHandler USER_HANDLER = new UserHandler();
    private static final GameHandler GAME_HANDLER = new GameHandler();
    private static final ClearHandler CLEAR_HANDLER = new ClearHandler();
    private static final WebSocketHandler WEB_SOCKET_HANDLER = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes() {
        Spark.webSocket("/ws", WEB_SOCKET_HANDLER);
        Spark.post("/session", USER_HANDLER::login);
        Spark.post("/user" , USER_HANDLER::register);
        Spark.delete("/session", USER_HANDLER::logout);

        Spark.get("/game", GAME_HANDLER::listGames);
        Spark.post("/game", GAME_HANDLER::createGame);
        Spark.put("/game", GAME_HANDLER::joinGame);

        Spark.delete("/db", CLEAR_HANDLER::clear);
    }
}
