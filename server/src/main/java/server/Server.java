package server;

import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import spark.*;

public class Server {
    private static final UserHandler userHandler = new UserHandler();
    private static final GameHandler gameHandler = new GameHandler();
    private static final ClearHandler clearHandler = new ClearHandler();

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
        Spark.post("/session", userHandler::login);
        Spark.post("/user" , userHandler::register);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.delete("/db", clearHandler::clear);
    }
}
