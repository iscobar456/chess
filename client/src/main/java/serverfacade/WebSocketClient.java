package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.*;
import org.glassfish.tyrus.client.ClientManager;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint {
    ServerMessageHandler handler;
    Gson gson;
    ClientManager client;
    Session session;

    public WebSocketClient(ServerMessageHandler handler, String url) throws Exception {
        try {
            gson = new Gson();
            this.handler = handler;

            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    handler.onMessage(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Client.ResponseException();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }
}
