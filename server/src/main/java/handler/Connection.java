package handler;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Integer gameID;
    public Session session;
    public String username;

    public Connection(String authToken, Integer gameID, Session session, String username) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
        this.username = username;
    }

//    public void send(String msg) throws IOException {
//        session.getRemote().sendString(msg);
//    }
}
