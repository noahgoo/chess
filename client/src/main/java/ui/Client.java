package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import net.MessageHandler;
import net.ServerFacade;
import net.WebSocketFacade;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

// has the menu
public class Client implements MessageHandler {
    static boolean quit = false;
    static String authToken;
    static ServerFacade serverFacade;
    private static WebSocketFacade wsf;
    private static String serverUrl = "http://localhost:8080";
    private static MessageHandler messageHandler;
    private static String username;
    private static String playerColor = "";
    private ChessGame board;
    int currentGameID;
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        //var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new Client().start(serverUrl);
    }

    private void start(String serverUrl) {
        System.out.println("Welcome to Noah's Chess Server\n");
        serverFacade = new ServerFacade(serverUrl);
        messageHandler = this;
        displayPreLoginUI();
    }

    private void displayPreLoginUI() {
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
            clearScanner(scanner);
            // check response is valid and if not ask again
            String[] validResponses = {"1", "2", "3", "4"};
            while (!Arrays.asList(validResponses).contains(response)) {
                out.print("Not a valid option, please try again: ");
                response = scanner.next();
                clearScanner(scanner);
            }

            switch (response) {
                case "1":
                    RegisterRequest registerRequest = getRegisterRequest(out, scanner);
                    if (registerRequest==null) {
                        out.println("Error: not a valid Register request\n");
                        break;
                    }
                    try {
                        RegisterResult registerResult = serverFacade.register(registerRequest);
                        out.println("Logged in as " + registerResult.username() + "\n");
                        authToken = registerResult.authToken();
                        username = registerResult.username();
                        displayPostLoginUI(out, scanner);
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
                        LoginResult loginResult = serverFacade.login(loginRequest);
                        out.println("Logged in as " + loginResult.username() + "\n");
                        authToken = loginResult.authToken();
                        username = loginResult.username();
                        displayPostLoginUI(out, scanner);
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
                            Enter "4" to quit
                            """);
                    break;
                case "4":
                    System.exit(0);
            }

            if (quit) {
                System.exit(0);
            }
        }
    }

    private RegisterRequest getRegisterRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide <USERNAME> <PASSWORD> <EMAIL>");
        out.print(">> ");
        String[] responses = scanner.nextLine().split("\\s+");
        if (responses.length==3) {
            return new RegisterRequest(responses[0], responses[1], responses[2]);
        }
        return null;
    }

    private LoginRequest getLoginRequest(PrintStream out, Scanner scanner) {
        out.println("\nPlease provide your <USERNAME> <PASSWORD>");
        out.print(">> ");
        String[] response = scanner.nextLine().split("\\s+");
        if (response.length==2) {
            return new LoginRequest(response[0], response[1]);
        }
        return null;
    }

    private CreateGameRequest getCreateRequest(PrintStream out, Scanner scanner) {
        out.println("Please provide a <GAMENAME>");
        out.print(">> ");
        return new CreateGameRequest(scanner.nextLine());
    }

    private JoinGameRequest getJoinRequest(PrintStream out, Scanner scanner) {
        out.println("Please provide <PLAYERCOLOR> <GAMEID>");
        out.print(">> ");
        if (scanner.hasNext()) {
            String playerColor = scanner.next();
            if (scanner.hasNextInt()) {
                int id = scanner.nextInt();
                scanner.nextLine();
                return new JoinGameRequest(playerColor, id);
            }
        }
        return null;
    }

    private void displayGamesList(PrintStream out, ListGameResult listResult) {
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

    private void displayChessBoard(ChessGame game, ChessPosition highlightPosition) {
        if (game!=null&&(playerColor.isEmpty()||playerColor.equals("WHITE"))) {
            ChessBoard.drawChessBoard(game, false, highlightPosition);
        } else if (game!=null&&playerColor.equals("BLACK")) {
            ChessBoard.drawChessBoard(game, true, highlightPosition);
        } else {
            System.out.println("Error: no game found");
        }
    }

    private void initiateWSF(String url) throws ResponseException {
        wsf = new WebSocketFacade(url, messageHandler);
    }

    private void displayPostLoginUI(PrintStream out, Scanner scanner) {
        while (true) {
            // clearScanner(scanner);
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
            clearScanner(scanner);
            String[] validResponses = {"1", "2", "3", "4", "5", "6", "7"};
            while (!Arrays.asList(validResponses).contains(response)) {
                out.print("Not a valid option, please try again: ");
                response = scanner.next();
                clearScanner(scanner);
            }

            switch (response) {
                case "1":
                    CreateGameRequest createGameRequest = getCreateRequest(out, scanner);
                    try {
                        CreateGameResult gameResult = serverFacade.createGame(createGameRequest, authToken);
                        out.println("Successfully created game with ID " + gameResult.gameID() + "\n");
                    } catch (ResponseException e) {
                        out.println("Error: could not create new game");
                    }
                    break;
                case "2":
                    try {
                        ListGameResult listResult = serverFacade.listGames(authToken);
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
                        serverFacade.joinGame(joinRequest, authToken);
                        out.println("Successfully joined game " + joinRequest.gameID() + "\n");
                        initiateWSF(serverUrl);
                        wsf.connect(authToken, joinRequest.gameID());
                        playerColor = joinRequest.playerColor();
                        currentGameID = joinRequest.gameID();
                        delay(250);
                        displayGameUI(out, scanner, joinRequest.gameID());
                    } catch (ResponseException e) {
                        out.println(e.getMessage());
                    }
                    break;
                case "4":
                    out.print("Please enter GAMEID: ");
                    if (scanner.hasNextInt()) {
                        currentGameID = scanner.nextInt();
                        clearScanner(scanner);
                        try {
                            initiateWSF(serverUrl);
                            wsf.connect(authToken, currentGameID);
                            delay(250);
                            displayGameUI(out, scanner, currentGameID);
                        } catch (ResponseException e) {
                            out.println(e.getMessage());
                        }
                    } else {
                        out.println("Invalid gameID");
                        break;
                    }
                    break;
                case "5":
                    try {
                        serverFacade.logout(authToken);
                        displayPreLoginUI();
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
                    quit = true;
                    return;
            }
        }
    }

    private static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayGameUI(PrintStream out, Scanner scanner, int gameID) {
        while (true) {
            out.println("[IN GAME]");
            out.println("\t1. Help");
            out.println("\t2. Redraw Chessboard");
            out.println("\t3. Leave");
            out.println("\t4. Make Move");
            out.println("\t5. Resign");
            out.println("\t6. Highlight Legal Moves");
            out.print(">> ");

            String response = scanner.next();
            clearScanner(scanner);
            String[] validResponses = {"1", "2", "3", "4", "5", "6"};
            while (!Arrays.asList(validResponses).contains(response)) {
                out.print("Not a valid option, please try again: ");
                response = scanner.next();
                clearScanner(scanner);
            }

            switch (response) {
                case "1":
                    // help
                    out.println("""
                            This is the Game Menu!
                            Enter "2" to redraw the Chessboard on your screen.
                            Enter "3" to leave the game.
                            Enter "4" to make a move with a piece.
                            Enter "5" to resign and lose the game.
                            Enter "6" to highlight legal moves for a piece.
                            """);
                    break;
                case "2":
                    // redraw the chessboard
                    displayChessBoard(board, null);
                    break;
                case "3":
                    // leave
                    out.println("Are you sure you want to leave? (y/n)");
                    String check = scanner.nextLine();
                    if (check.equals("n")) {
                        break;
                    } else if (!check.equals("y")) {
                        out.println("Invalid answer");
                    } else {
                        try {
                            wsf.leave(authToken, gameID);
                            return;
                        } catch (ResponseException e) {
                            out.println("Unauthorized");
                        }
                    }
                    break;
                case "4":
                    ChessMove playerMove = getMoveRequest(out, scanner);
                    if (playerMove.getPromotionPiece()!=null&&!checkValidPromotion(playerMove.getStartPosition(), playerMove.getEndPosition())) {
                        out.println("Error: not a valid promotion");
                        break;
                    }
                    try {
                        wsf.makeMove(authToken, gameID, playerMove);
                    } catch (ResponseException e) {
                        out.printf("Error: %s%n", e.getMessage());
                    }
                    break;
                case "5":
                    // resign
                    out.println("Are you sure you want to resign? (y/n)");
                    String resp = scanner.nextLine();
                    if (resp.equals("n")) {
                        break;
                    } else if (!resp.equals("y")) {
                        out.println("Invalid answer");
                    } else {
                        try {
                            wsf.resign(authToken, gameID);
                        } catch (ResponseException e) {
                            out.println("Unauthorized");
                        }
                    }
                    break;
                case "6":
                    ChessPosition piecePosition = getHighlightPiece(out, scanner);
                    displayChessBoard(board, piecePosition);
                    break;
            }
            delay(500);
        }
    }

    private ChessPosition getHighlightPiece(PrintStream out, Scanner scanner) {
        int row = askRow(out, scanner, "piece");
        int col = askColumn(out, scanner, "piece");
        return new ChessPosition(row, col);
    }

    private ChessMove getMoveRequest(PrintStream out, Scanner scanner) {
        int startRow = askRow(out, scanner, "starting");
        int startCol = askColumn(out, scanner, "starting");
        int endRow = askRow(out, scanner, "ending");
        int endCol = askColumn(out, scanner, "ending");
        scanner.nextLine();
        ChessPiece.PieceType promotionPiece = askPromotionPiece(out, scanner);


        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);
        return new ChessMove(start, end, promotionPiece);
    }

    private boolean checkValidPromotion(ChessPosition start, ChessPosition end) {
        chess.ChessBoard chessBoard = board.getBoard();
        if (chessBoard.getPiece(start)!=null) {
            ChessPiece startPiece = chessBoard.getPiece(start);
            if (startPiece.getPieceType().equals(ChessPiece.PieceType.PAWN)
                    &&startPiece.getTeamColor().toString().equals(playerColor)) {
                if (playerColor.equals("WHITE")&&end.getRow()==8) {
                    return true;
                } else return playerColor.equals("BLACK") && end.getRow() == 1;
            }
        }
        return false;
    }

    private ChessPiece.PieceType askPromotionPiece(PrintStream out, Scanner scanner) {
        out.println("If you are moving your pawn and it can be promoted please enter the following:");
        out.println("Enter 'q' for Queen, 'r' for Rook, 'b' for Bishop, or 'n' for Knight:");
        out.println("Other press ENTER to to skip!");

        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                return null;
            }

            switch (input) {
                case "q" -> { return ChessPiece.PieceType.QUEEN; }
                case "r" -> { return ChessPiece.PieceType.ROOK; }
                case "b" -> { return ChessPiece.PieceType.BISHOP; }
                case "n" -> { return ChessPiece.PieceType.KNIGHT; }
                default -> out.println("Invalid input. Press ENTER to skip promotion or please enter 'q', 'r', 'b', or 'n':");
            }
        }
    }

    private int askRow(PrintStream out, Scanner scanner, String label) {
        int row = -1;
        while (row < 1 || row > 8) {
            out.printf("Enter the %s row (1-8): ", label);
            if (scanner.hasNextInt()) {
                row = scanner.nextInt();
            } else {
                scanner.next();
                out.println("Error: Invalid input. Please enter a number from 1 to 8.");
            }
        }
        return row;
    }

    private int askColumn(PrintStream out, Scanner scanner, String label) {
        String input = "";
        char colChar;
        while (true) {
            out.printf("Enter the %s column (a-h): ", label);
            input = scanner.next().toLowerCase();
            if (input.length() == 1) {
                colChar = input.charAt(0);
                if (colChar >= 'a' && colChar <= 'h') {
                    return colChar - 'a' + 1;
                }
            }
            out.println("Error: Invalid input. Please enter a single letter from a to h.");
        }
    }

    private void clearScanner(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private void displayNotification(String msg) {
        System.out.printf("%n%s%n", msg);
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) serverMessage).getNotification());
            case ERROR -> displayNotification(((ErrorMessage) serverMessage).getError());
            case LOAD_GAME -> {
                LoadGameMessage load = (LoadGameMessage) serverMessage;
                displayChessBoard(load.getGame(), null);
                board = load.getGame();
                // logger.warning("Received and displayed ChessGame");
            }
        }
    }
}
