package net;

import com.google.gson.Gson;
import exception.ResponseException;
import ui.ChessBoard;
import ui.Client;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import javax.imageio.IIOException;
import javax.websocket.MessageHandler;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    net.MessageHandler messageHandler;

    public WebSocketFacade(net.MessageHandler handler, String url) throws ResponseException {
        this.messageHandler = handler;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    // handle message here
                    try {
                        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                        messageHandler.notify(msg);
                    } catch (Exception e) {
                        messageHandler.notify(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                                "Error: unable to process incoming message"));
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(500, "WebSocket error: " + e.getClass().getSimpleName() + " - " + e.getMessage());

        }
    }

    public void connect(String authToken, Integer gameID, String username) throws ResponseException {
        try {
            var connectCommand = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, String username) throws ResponseException {
        try {
            var moveCommand = new UserGameCommand(CommandType.MAKE_MOVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID, String username) throws ResponseException {
        try {
            var leaveCommand = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID, String username) throws ResponseException {
        try {
            var resignCommand = new UserGameCommand(CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
