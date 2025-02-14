package server;

import Handler.LoginHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import spark.*;

public class Server {
    private static final LoginHandler loginHandler = new LoginHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes() {
        Spark.get("/session", (request, response) -> loginHandler.login(request, response));
    }
}
