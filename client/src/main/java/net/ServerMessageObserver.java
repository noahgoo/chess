package net;

import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ServerMessageObserver {
    public void sendServerMessage(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            // case LOAD_GAME -> ChessBoard.(((LoadGameMessage) serverMessage).getGame());
            case NOTIFICATION -> displayMessage(((NotificationMessage) serverMessage).getNotification());
            case ERROR -> displayMessage(((ErrorMessage) serverMessage).getError());
        }
    }

    private void displayMessage(String message) {
        System.out.println(message);
    }
}
