package handler;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.Service;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import java.io.IOException;
import java.util.Collection;

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
            case RESIGN -> resign(command, session);
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        ChessGame game;
        // get username from db
        AuthData authData = getAuthData(command, session);
        if (authData == null) {
            return;
        }
        connections.add(command, session, authData.username());
        String playerColor;
        try {
            game = getGame(command.getGameID()).game();
            playerColor = getPlayerColor(authData, getGame(command.getGameID()));
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: bad gameID", authData.username());
            return;
        }

        String message = String.format("%s has connected to the game as [%s]", authData.username(), playerColor);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command, notificationMessage, false);
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
        connections.notifyUser(command, loadGameMessage, authData.username());

    }

    private GameData getGame(Integer gameID) throws DataAccessException {
        try {
            return Service.GAME_DAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw e;
        }
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException {
        AuthData authData = getAuthData(command, session);
        if (authData == null) {
            return;
        }
        // 1. verify validity of move
        GameData gameData;
        ChessGame game;
        TeamColor color;
        try {
            gameData = getGame(command.getGameID());
            game = gameData.game();
            if (checkObserver(authData, gameData)) {
                connections.sendError(command, "Error: Observer can't make move", authData.username());
                return;
            }
            color = game.getTeamTurn();
            if (!checkIsTurn(authData, gameData, color)) {
                connections.sendError(command, "Error: Not your turn", authData.username());
                return;
            }
            var startMove = command.getMove().getStartPosition();
            var validMoves = game.validMoves(startMove);
            boolean valid = false;
            if (validMoves!=null) {
                valid = isValid(command, validMoves, game, startMove, color, gameData, authData, valid);
            } else {
                connections.sendError(command, "Error: no piece in starting position", authData.username());
                return;
            }
            if (!valid) {
                connections.sendError(command, "Error: not a valid move", authData.username());
                return;
            } else if (game.resign) {
                connections.sendError(command, "Error: game is already over", authData.username());
                return;
            }
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: bad gameID", authData.username());
            return;
        }
        // 2. update game to represent move
        try {
            game.makeMove(command.getMove());
            GameData oldData = Service.GAME_DAO.getGame(command.getGameID());
            GameData newData = new GameData(oldData.gameID(), oldData.whiteUsername(),
                    oldData.blackUsername(), oldData.gameName(), game);
            Service.GAME_DAO.updateGame(color.toString(), newData, null);
        } catch (InvalidMoveException | DataAccessException e) {
            connections.sendError(command, "Error: unable to update game", authData.username());
            return;
        }
        // 3. send LOAD_GAME message to all clients
        LoadGameMessage loadMessage = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
        connections.broadcast(command, loadMessage, true);
        // 4. send NOTIFICATION to all other clients to say what move happened
        String start = getMoveString(command.getMove().getStartPosition());
        String end = getMoveString(command.getMove().getEndPosition());
        String message = String.format("Move made by %s from %s to %s%n", authData.username(), start, end);
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command, notificationMessage, false);
        // 5. if results in check, checkmate, or stalemate a NOTIFICATION is sent to all clients
        String gameMessage = null;
        if (game.isInCheck(TeamColor.BLACK)) {
            if (game.isInCheckmate(TeamColor.BLACK)) {
                gameMessage = String.format("Player [%s] is in Checkmate", gameData.blackUsername());
            } else if (game.isInStalemate(TeamColor.BLACK)) {
                gameMessage = String.format("Player [%s] is in Stalemate", gameData.blackUsername());
            } else {
                gameMessage = String.format("Player [%s] is in Check", gameData.blackUsername());
            }
        }
        if (game.isInCheck(TeamColor.WHITE)) {
            if (game.isInCheckmate(TeamColor.WHITE)) {
                gameMessage = String.format("Player [%s] is in Checkmate", gameData.whiteUsername());
            } else if (game.isInStalemate(TeamColor.WHITE)) {
                gameMessage = String.format("Player [%s] is in Stalemate", gameData.whiteUsername());
            } else {
                gameMessage = String.format("Player [%s] is in Check", gameData.whiteUsername());
            }
        }
        if (gameMessage!=null) {
            System.out.printf("This is the gameMessage: %s", gameMessage);
            NotificationMessage checkNotification = new NotificationMessage(ServerMessageType.NOTIFICATION, gameMessage);
            connections.broadcast(command, checkNotification, true);
        }
    }

    private boolean isValid(MakeMoveCommand command, Collection<ChessMove> validMoves, ChessGame game, ChessPosition startMove, TeamColor color, GameData gameData, AuthData authData, boolean valid) {
        for (var move: validMoves) {
            var boardPiece = game.getBoard().getPiece(startMove);
            if (move.getEndPosition().equals(command.getMove().getEndPosition())&& color.equals(boardPiece.getTeamColor())) {
                if (color.equals(TeamColor.WHITE)&&(gameData.whiteUsername().equals(authData.username()))) {
                    valid = true;
                    break;
                } else if (color.equals(TeamColor.BLACK)&&(gameData.blackUsername().equals(authData.username()))) {
                    valid = true;
                    break;
                }
            }
        }
        return valid;
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        AuthData authData = getAuthData(command, session);
        if (authData == null) {
            return;
        }
        // 1. If a player is leaving, then the game is updated to remove the root client. Game is updated in the database.
        try {
            updatePlayers(command, authData);
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: unable to update game", authData.username());
        }
        // 2. send NOTIFICATION to all other clients that root client left.
        String notification = String.format("%s has left the game", authData.username());
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(command, notificationMessage, false);
        connections.remove(command);
        session.close();
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        AuthData authData = getAuthData(command, session);
        if (authData == null) {
            return;
        }
        // 1. server marks the game as over, no more moves can be made, game updated in db
        boolean observer = true;
        GameData data;
        try {
            data = getGame(command.getGameID());
            if (data.game().resign) {
                connections.sendError(command, "Error: already resigned", authData.username());
                return;
            }
            if (data.whiteUsername()!=null&&data.whiteUsername().equals(authData.username())) {
                observer = false;
            } else if (data.blackUsername()!=null&&data.blackUsername().equals(authData.username())) {
                observer = false;
            }
        } catch (DataAccessException e) {
            connections.sendError(command, "Error: game not found", authData.username());
            return;
        }
        if (observer) {
            connections.sendError(command, "Error: observer cannot resign", authData.username());
            return;
        } else {
            data.game().resign = true;
            try {
                Service.GAME_DAO.updateGame(null, data, authData);
            } catch (DataAccessException e) {
                connections.sendError(command, "Error: unable to update game", authData.username());
                return;
            }
        }

        // 2. sends NOTIFICATION to all clients informing them root client resigned
        String notification = String.format("%s has resigned from the game", authData.username());
        NotificationMessage notificationMessage = new NotificationMessage(ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(command, notificationMessage, true);
    }

    private AuthData getAuthData(UserGameCommand command, Session session) throws IOException {
        AuthData authData = Service.AUTH_DAO.getAuth(command.getAuthToken());
        if (authData == null) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized");
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return null;
        }
        return authData;
    }

    private void updatePlayers(UserGameCommand command, AuthData authData) throws DataAccessException {
        GameData gameData = Service.GAME_DAO.getGame(command.getGameID());
        if (gameData.whiteUsername()!=null&&gameData.whiteUsername().equals(authData.username())) {
            Service.GAME_DAO.updateGame("WHITE", gameData, authData);
        } else if (gameData.blackUsername()!=null&&gameData.blackUsername().equals(authData.username())) {
            Service.GAME_DAO.updateGame("BLACK", gameData, authData);
        }
    }

    private boolean checkObserver(AuthData authData, GameData data) {
        if (data.whiteUsername()!=null&&authData.username().equals(data.whiteUsername())) {
            return false;
        } else return data.blackUsername() == null || !authData.username().equals(data.blackUsername());
    }

    private String getPlayerColor(AuthData authData, GameData data) {
        if (data.whiteUsername()!=null&&authData.username().equals(data.whiteUsername())) {
            return "WHITE player";
        } else if (data.blackUsername()!=null&&authData.username().equals(data.blackUsername())) {
            return "BLACK player";
        }
        return "OBSERVER";
    }

    private boolean checkIsTurn(AuthData authData, GameData data, TeamColor color) {
        if (data.whiteUsername()!=null&&authData.username().equals(data.whiteUsername())&&color.equals(TeamColor.WHITE)) {
            return true;
        } else return data.blackUsername() != null && authData.username().equals(data.blackUsername()) && color.equals(TeamColor.BLACK);
    }

    private String getMoveString(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        char colLetter = (char) ('a' + col - 1);
        return String.format("[%s%s]", colLetter, row);
    }
}
