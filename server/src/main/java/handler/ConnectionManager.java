package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(UserGameCommand command, Session session) {
        Integer gameID = command.getGameID();
        Connection conn = new Connection(command.getAuthToken(), gameID, session);

        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(conn);
        // System.out.printf("[Size] of connections Hash: %d%n", connections.size());
    }

    public void remove(UserGameCommand command) {
        Set<Connection> set = connections.get(command.getGameID());
        for (Connection conn: set) {
            if (conn.authToken.equals(command.getAuthToken())) {
                connections.get(command.getGameID()).remove(conn);
            }
        }
        if (set.isEmpty()) {
            connections.remove(command.getGameID());
        }
    }

    public void broadcast(UserGameCommand command, String message) throws IOException {
        ArrayList<Connection> removalList = new ArrayList<>();
        Set<Connection> set = connections.get(command.getGameID());
        for (Connection c: set) {
             if (c.session.isOpen()) {
//                 System.out.printf("Session was open for game: %d and auth: %s%n", c.gameID, c.authToken);
                if (!c.authToken.equals(command.getAuthToken())) {
                    NotificationMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    c.send(new Gson().toJson(msg));
//                    System.out.println("Notification was sent");
                }
            } else {
                removalList.add(c);
            }
        }
        for (Connection c: removalList) {
            connections.get(command.getGameID()).remove(c);
//            System.out.printf("Connection removed for auth: %s%n", c.authToken);
        }
    }

    public void notifyUser(UserGameCommand command, GameDAO dao) throws IOException {
        Connection userConn = getConnection(command);
        try {
            if (userConn != null) {
                ChessGame game = dao.getGame(command.getGameID()).game();
                LoadGameMessage msg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                userConn.send(new Gson().toJson(msg));
//                System.out.printf("Game was sent to %s%n", command.getAuthToken());
//                System.out.println(msg);
            } else {
                throw new IOException("Error: no connection found");
            }
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: unable to access game");
            userConn.send(new Gson().toJson(error));
        }
    }

    private Connection getConnection(UserGameCommand command) {
        // get the connection of the user
        Set<Connection> set = connections.get(command.getGameID());
        Connection userConn = null;
        for (Connection c: set) {
            if (c.authToken.equals(command.getAuthToken())) {
                userConn = c;
                break;
            }
        }
        return userConn;
    }

    public void sendError(UserGameCommand command, String message) throws IOException {
        Connection conn = getConnection(command);
        ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        conn.send(new Gson().toJson(msg));
    }
}
