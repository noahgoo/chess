package websocket.messages;

public class NotificationMessage extends ServerMessage {
    String message;

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getNotification() { return this.message; }
}
