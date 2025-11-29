package serverfacade;

import websocket.messages.ServerMessage;

public interface MessageHandler {
    public void onMessage(ServerMessage message);
}
