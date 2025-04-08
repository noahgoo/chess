package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage(ServerMessageType type, String error) {
        super(type);
        this.errorMessage = error;
    }
}
