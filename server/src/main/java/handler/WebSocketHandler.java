package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.Service;
import spark.Request;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler extends Handler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                makeMove(moveCommand, session);
            }
            case LEAVE -> leave(command, session);
            case RESIGN -> {}
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        connections.add(command, session);
        // get username from db
        AuthData authData = getAuthData(command);
        if (authData == null) {
            return;
        }
        try {
            getGame(command.getGameID());
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: bad gameID");
            return;
        }
        String message = String.format("%s has connected to the game", authData.username());
        connections.broadcast(command, message);
        connections.notifyUser(command, Service.GAME_DAO);

    }

    private ChessGame getGame(Integer gameID) throws DataAccessException {
        try {
            return Service.GAME_DAO.getGame(gameID).game();
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        // 1. verify validity of move
        ChessGame game;
        try {
            game = getGame(command.getGameID());
            var startMove = command.getMove().getStartPosition();
            var validMoves = game.validMoves(startMove);
            boolean valid = false;
            for (var move: validMoves) {
                if (move.getEndPosition().equals(command.getMove().getEndPosition())) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                connections.sendError(command, "Error: not a valid move");
                return;
            }
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: bad gameID");
            return;
        }
        // 2. update game to represent move
        try {
            var color = game.getTeamTurn();
            game.makeMove(command.getMove());
            GameData gameData = Service.GAME_DAO.getGame(command.getGameID());
            GameData newData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);
            Service.GAME_DAO.updateGame(color.toString(), newData, null);
        } catch (InvalidMoveException | DataAccessException e) {

        }
        // 3. send LOAD_GAME message to all clients

        // 4. send NOTIFICATION to all other clients to say what move happened

        // 5. if results in check, checkmate, or stalemate a NOTIFICATION is sent to all clients
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        AuthData authData = getAuthData(command);
        if (authData == null) {
            return;
        }
        // update game
        updateGame(command, authData);
        // message all players
        String notification = String.format("%s has left the game", authData.username());
        connections.broadcast(command, notification);
        connections.remove(command);
        session.close();
    }

    private AuthData getAuthData(UserGameCommand command) throws IOException {
        AuthData authData = Service.AUTH_DAO.getAuth(command.getAuthToken());
        if (authData==null) {
            String errorMessage = "Error: unauthorized";
            connections.sendError(command, errorMessage);
            return null;
        }
        return authData;
    }

    private void updateGame(UserGameCommand command, AuthData authData) {
        try {
            GameData gameData = Service.GAME_DAO.getGame(command.getGameID());
            if (gameData.whiteUsername().equals(authData.username())) {
                // figure out how to update game so user is null again
            } else if (gameData.blackUsername().equals(authData.username())) {

            }
        } catch (DataAccessException e) {

        }
    }
}
