package serverfacade;

import websocket.messages.ServerMessage;

public interface ServerMessageHandler {
    public void onMessage(ServerMessage message);
}
