package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketClient {
    MessageHandler handler;
    Gson gson;
    ClientManager client;

    public WebSocketClient(MessageHandler handler, String serverURI) throws Exception {
        gson = new Gson();
        this.handler = handler;
        client = ClientManager.createClient();
        client.connectToServer(WebSocketEndpoint.class, URI.create(serverURI + "/ws"));
    }

    @ClientEndpoint
    class WebSocketEndpoint {
        @OnMessage
        public void onMessage(Session session, String message) {
            handler.onMessage(gson.fromJson(message, ServerMessage.class));
        }
    }
}
