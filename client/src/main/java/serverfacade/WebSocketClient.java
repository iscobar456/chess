package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import org.glassfish.grizzly.http.server.SessionManager;
import org.glassfish.tyrus.client.ClientManager;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketClient {
    MessageHandler handler;
    Gson gson;
    ClientManager client;
    Session session;

    public WebSocketClient(MessageHandler handler, String serverURI) throws Exception {
        gson = new Gson();
        this.handler = handler;
        client = ClientManager.createClient();
        session = client.connectToServer(WebSocketEndpoint.class, URI.create(serverURI + "/ws"));
    }

    @ClientEndpoint
    class WebSocketEndpoint {
        @OnMessage
        public void onMessage(Session session, String message) {
            handler.onMessage(gson.fromJson(message, ServerMessage.class));
        }
    }

    public void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }
}
