package handler;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(UserGameCommand command, Session session, String username) {
        Integer gameID = command.getGameID();
        var conn = new Connection(command.getAuthToken(), gameID, session, username);
        if (connections.contains(gameID)) {
            connections.get(gameID).add(conn);
        } else {
            connections.put(gameID, new HashSet<>(List.of(conn)));
        }
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
                if (!c.authToken.equals(command.getAuthToken())) {
                    c.session.getRemote().sendString(message);
                }
            } else {
                removalList.add(c);
            }
        }
        for (Connection c: removalList) {
            connections.get(command.getGameID()).remove(c);
        }
    }
}
