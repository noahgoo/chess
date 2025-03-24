package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import net.ServerFacade;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

// has the menu
public class Client {
    static boolean QUIT = false;
    static String AUTH_TOKEN;
    static ServerFacade SERVER_FACADE;

    public static void main(String[] args) {
//        var serverUrl = "http://localhost:8080";
//        if (args.length == 1) {
//            serverUrl = args[0];
//        }
//
//        new Server(serverUrl).run();
        SERVER_FACADE = new ServerFacade("http://localhost:8080");
        displayPreLogin();
    }

    private static void displayPreLogin() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(System.in);
        // display pre login menu
        while (true) {
            out.println("[Logged Out]");
            out.println("\t1. Register");
            out.println("\t2. Login");
            out.println("\t3. Help");
            out.println("\t4. Quit");
            out.print(">> ");

            String response = scanner.next();
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            // check response is valid and if not ask again
            String[] validResponses = {"1", "2", "3", "4"};
            while (!Arrays.asList(validResponses).contains(response)) {
                out.print("Not a valid option, please try again: ");
                response = scanner.next();
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }

            switch (response) {
                case "1":
                    RegisterRequest registerRequest = getRegisterRequest(out, scanner);
                    if (registerRequest==null) {
                        out.println("Error: not a valid Register request\n");
                        break;
                    }
                    try {
                        RegisterResult registerResult = SERVER_FACADE.register(registerRequest);
                        out.println("Logged in as " + registerResult.username() + "\n");
                        AUTH_TOKEN = registerResult.authToken();
                        displayPostLogin(out, scanner);
                    } catch (ResponseException e) {
                        out.println("Error: could not process Register request\n");
                        break;
                    }
                    break;
                case "2":
                    LoginRequest loginRequest = getLoginRequest(out, scanner);
                    if (loginRequest==null) {
                        out.println("Error: not a valid login request\n");
                        break;
                    }
                    try {
                        LoginResult loginResult = SERVER_FACADE.login(loginRequest);
                        out.println("Logged in as " + loginResult.username() + "\n");
                        AUTH_TOKEN = loginResult.authToken();
                        displayPostLogin(out, scanner);
                    } catch (ResponseException e) {
                        out.println("Error: could not process Login request\n");
                        break;
                    }
                    break;
                case "3":
                    out.println(""" 
                            This is the Chess Pre-Login Menu!
                            Enter "1" to register a new user with a given USERNAME, PASSWORD, and EMAIL
                            Enter "2" to login an existing user with a given USERNAME and PASSWORD
                            Enter "3" to quit
                            """);
                    break;
                case "4":
                    QUIT = true;
            }

            if (QUIT) {
                return;
            }
        }
    }

    private static RegisterRequest getRegisterRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide <USERNAME> <PASSWORD> <EMAIL>");
        out.print(">> ");
        String[] responses = scanner.nextLine().split("\\s+");
        if (responses.length==3) {
            return new RegisterRequest(responses[0], responses[1], responses[2]);
        }
        return null;
    }

    private static LoginRequest getLoginRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide your <USERNAME> <PASSWORD>");
        out.print(">> ");
        String[] response = scanner.nextLine().split("\\s+");
        if (response.length==2) {
            return new LoginRequest(response[0], response[1]);
        }
        return null;
    }

    private static CreateGameRequest getCreateRequest(PrintStream out, Scanner scanner) {
        out.println("Please provide a <GAMENAME>");
        out.print(">> ");
        return new CreateGameRequest(scanner.nextLine());
    }

    private static JoinGameRequest getJoinRequest(PrintStream out, Scanner scanner) {
        out.println("Please provide <PLAYERCOLOR> <GAMEID>");
        out.print(">> ");
        if (scanner.hasNext()) {
            String playerColor = scanner.next();
            if (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                return new JoinGameRequest(playerColor, id);
            }
        }
        return null;
    }

    private static void displayGamesList(PrintStream out, ListGameResult listResult) {
        out.println("GAMEID | GAMENAME | WHITEUSER | BLACKUSER");
        for (GameData game: listResult.games()) {
            String builder = game.gameID() +
                    " | " +
                    game.gameName() +
                    " | " +
                    game.whiteUsername() +
                    " | " +
                    game.blackUsername();
            out.println(builder);
        }
        out.println();
    }

    private static void displayChessBoard(ListGameResult listResult, JoinGameRequest joinRequest) {
        ChessGame currentGame = null;
        for (GameData game: listResult.games()) {
            if (game.gameID()==joinRequest.gameID()) {
                currentGame = game.game();
            }
        }
        if (currentGame!=null&&joinRequest.playerColor().equals("WHITE")) {
            ChessBoard.drawChessBoard(currentGame.getBoard(), false);
        } else if (currentGame!=null&&joinRequest.playerColor().equals("BLACK")) {
            ChessBoard.drawChessBoard(currentGame.getBoard(), true);
        }

    }

    private static void displayPostLogin(PrintStream out, Scanner scanner) {
        while (true) {
            out.println("[Logged In]");
            out.println("\t1. Create Game");
            out.println("\t2. List Games");
            out.println("\t3. Join Game");
            out.println("\t4. Observe Game");
            out.println("\t5. Logout");
            out.println("\t6. Help");
            out.println("\t7. Quit");
            out.print(">> ");

            String response = scanner.next();
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            String[] validResponses = {"1", "2", "3", "4", "5", "6", "7"};
            while (!Arrays.asList(validResponses).contains(response)) {
                out.print("Not a valid option, please try again: ");
                response = scanner.next();
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }

            switch (response) {
                case "1":
                    CreateGameRequest createGameRequest = getCreateRequest(out, scanner);
                    try {
                        CreateGameResult gameResult = SERVER_FACADE.createGame(createGameRequest, AUTH_TOKEN);
                        out.println("Successfully created game with ID " + gameResult.gameID() + "\n");
                    } catch (ResponseException e) {
                        out.println("Error: could not create new game");
                    }
                    break;
                case "2":
                    try {
                        ListGameResult listResult = SERVER_FACADE.listGames(AUTH_TOKEN);
                        displayGamesList(out, listResult);
                    } catch (ResponseException e) {
                        out.println("Error: couldn't provide list of games");
                    }
                    break;
                case "3":
                    JoinGameRequest joinRequest = getJoinRequest(out, scanner);
                    if (joinRequest==null) {
                        out.println("Error: not a valid join request\n");
                        break;
                    }
                    try {
                        SERVER_FACADE.joinGame(joinRequest, AUTH_TOKEN);
                        out.println("Successfully joined game " + joinRequest.gameID() + "\n");
                        displayChessBoard(SERVER_FACADE.listGames(AUTH_TOKEN), joinRequest);
                    } catch (ResponseException e) {
                        out.println(e.getMessage());
                    }
                    break;
                case "4":
                    out.print("Please enter GAMEID: ");
                    int gameid = scanner.nextInt();
                    JoinGameRequest observeRequest = new JoinGameRequest("WHITE", gameid);
                    try {
                        displayChessBoard(SERVER_FACADE.listGames(AUTH_TOKEN), observeRequest);
                    } catch (ResponseException e) {
                        out.println("Error: unable to observe game " + observeRequest.gameID());
                    }
                    break;
                case "5":
                    try {
                        SERVER_FACADE.logout(AUTH_TOKEN);
                        displayPreLogin();
                    } catch (ResponseException e) {
                        out.println(e.getMessage());
                        break;
                    }
                    break;
                case "6":
                    out.println(""" 
                            This is the Chess Menu!
                            Enter "1" to create a new game with given GAMENAME
                            Enter "2" to list all existing games
                            Enter "3" to join a game with a given GAMEID
                            Enter "4" to observe a game with a given GAMEID
                            Enter "5" to logout
                            Enter "7" to quit
                            """);
                    break;
                case "7":
                    QUIT = true;
                    return;
            }
        }
    }

}
