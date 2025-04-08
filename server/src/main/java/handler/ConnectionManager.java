package handler;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(UserGameCommand command, Session session, String username) {
        var conn = new Connection(command.getAuthToken(), command.getGameID(), session, username);
        connections.put(command.getAuthToken(), conn);
    }

    public void remove(UserGameCommand command) {
        connections.remove(command.getAuthToken());
    }

    public void broadcast(String username, ServerMessage type, String message) throws IOException {
        var removalList = new ArrayList<>();
        for (var c: connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(username)) {
                    c.session.getRemote().sendString(message);
                }
            } else {
                removalList.add(c);
            }
        }
        for (var c: removalList) {
            connections.remove(c);
        }
    }
}
