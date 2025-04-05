package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketMessage
    public void onMessage(String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> // connect
            case MAKE_MOVE -> // make move
            case LEAVE -> // leave
            case RESIGN -> // resign
        }
    }


}
