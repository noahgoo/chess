package client;

import exception.ResponseException;
import model.GameData;
import net.ServerFacade;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.ListGameResult;
import result.LoginResult;
import result.RegisterResult;
import server.Server;
import ui.Client;

import java.io.InputStreamReader;
import java.util.ArrayList;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerFail() {
        try {
            facade.clear(null);
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.register(new RegisterRequest("wrong", null, "wrong")));
            Assertions.assertEquals("Error: bad request", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerSuccess() {
        try {
            facade.clear(null);
            RegisterRequest request = new RegisterRequest("testUser", "testPass", "testEmail");
            RegisterResult actualResult = facade.register(request);
            RegisterResult expectedResult = new RegisterResult("testUser", actualResult.authToken());
            Assertions.assertEquals(expectedResult, actualResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginFail() {
        try {
            facade.clear(null);
            LoginRequest request = new LoginRequest("testUser", "testPass");
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.login(request));
            Assertions.assertEquals("Error: no existing user or wrong password", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginSuccess() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest request = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult actualResult = facade.login(request);
            LoginResult expectedResult = new LoginResult("testUser", actualResult.authToken());
            Assertions.assertEquals(expectedResult, actualResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void logoutFail() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            facade.login(loginRequest);
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.logout(null));
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void logoutSuccess() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            Assertions.assertDoesNotThrow(() -> {
                facade.logout(loginResult.authToken());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createGameFail() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest(null);
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.createGame(createGameRequest, loginResult.authToken()));
            Assertions.assertEquals(500, e.StatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createGameSuccess() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("testGame");
            CreateGameResult actualResult = facade.createGame(createGameRequest, loginResult.authToken());
            CreateGameResult expectedResult = new CreateGameResult(1);
            Assertions.assertEquals(expectedResult, actualResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listGamesFail() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            facade.login(loginRequest);
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.listGames(null));
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listGamesSuccess() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            ListGameResult actualGameResult = facade.listGames(loginResult.authToken());
            ListGameResult expectedGameResult = new ListGameResult(new ArrayList<>());
            Assertions.assertEquals(expectedGameResult, actualGameResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void joinGameFail() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            facade.createGame(new CreateGameRequest("testGame"), loginResult.authToken());
            JoinGameRequest joinGameRequest = new JoinGameRequest(null, 1);
            ResponseException e = Assertions.assertThrows(ResponseException.class, () ->
                    facade.joinGame(joinGameRequest, loginResult.authToken()));
            Assertions.assertEquals("Error: bad request", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void joinGameSuccess() {
        try {
            facade.clear(null);
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            facade.createGame(new CreateGameRequest("testGame"), loginResult.authToken());
            JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", 1);
            facade.joinGame(joinGameRequest, loginResult.authToken());
            ListGameResult listGameResult = facade.listGames(loginResult.authToken());
            Assertions.assertEquals(listGameResult.games().getFirst().blackUsername(), "testUser");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clearSuccess() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", null);
            LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
            facade.register(registerRequest);
            facade.login(loginRequest);
            Assertions.assertDoesNotThrow(() -> {
                facade.clear(null);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
