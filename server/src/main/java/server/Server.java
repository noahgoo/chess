package server;

import Handler.ClearHandler;
import Handler.GameHandler;
import Handler.UserHandler;
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

        //This line initializes the server and can be removed once you have a functioning endpoint 
        // Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes() {
        Spark.post("/session", (request, response) -> userHandler.login(request, response) );
        Spark.post("/user" , (request, response) -> userHandler.register(request, response) );
        Spark.delete("/session", (request, response) -> userHandler.logout(request, response));

        Spark.get("/game", (request, response) -> gameHandler.listGames(request, response));
        Spark.post("/game", (request, response) -> gameHandler.createGame(request, response));
        Spark.put("/game", (request, response) -> gameHandler.joinGame(request, response));

        Spark.delete("/db", (request, response) -> clearHandler.clear(request, response));
    }
}
