package server.websocket;

import com.google.gson.Gson;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final Service service;

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> {
                try {
                    service.validateToken(command.getAuthToken());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
            case MAKE_MOVE -> {
                try {
                    service.validateToken(command.getAuthToken());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
            case LEAVE -> {
                try {
                    service.validateToken(command.getAuthToken());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
            case RESIGN -> {
                try {
                    service.validateToken(command.getAuthToken());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}