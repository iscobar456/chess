package serverfacade;

import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import ui.UpdateReceiver;

public class WebSocketClient {
    UpdateReceiver listener;
    Gson gson;

    public WebSocketClient(UpdateReceiver listener) {
        gson = new Gson();
        this.listener = listener;
    }

    @ClientEndpoint
    class WebSocketEndpoint {
        @OnMessage
        public void onMessage(Session session, String message) {

        }
    }
}
