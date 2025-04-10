package net;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import javax.websocket.MessageHandler;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    net.MessageHandler messageHandler;

    public WebSocketFacade(String url, net.MessageHandler handler) throws ResponseException {
        messageHandler = handler;
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    // handle message here
                    handleMessage(message);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(500, "WebSocket error: " + e.getClass().getSimpleName() + " - " + e.getMessage());

        }
    }

    private void handleMessage(String message) {
        try {
            JsonObject object = JsonParser.parseString(message).getAsJsonObject();
            String typeString = object.get("serverMessageType").getAsString();
            ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(typeString);
            Gson gson = new Gson();
            ServerMessage msg = null;

            switch (type) {
                case LOAD_GAME -> msg = gson.fromJson(message, LoadGameMessage.class);
                case NOTIFICATION -> msg = gson.fromJson(message, NotificationMessage.class);
                case ERROR -> msg = gson.fromJson(message, ErrorMessage.class);
                default -> messageHandler.notify(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: not a valid incoming server message"));
            }
            if (msg!=null) {
                messageHandler.notify(msg);
            }
        } catch (Exception e) {
            messageHandler.notify(new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: unable to process incoming message"));
        }
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            var connectCommand = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var moveCommand = new MakeMoveCommand(CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            var leaveCommand = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
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
