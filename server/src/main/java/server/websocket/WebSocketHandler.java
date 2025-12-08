package server.websocket;

import chess.InvalidMoveException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final Service service;
    private HashMap<Integer, ArrayList<String>> observers = new HashMap<>();

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        System.out.println("received message");
        switch (command.getCommandType()) {
            case CONNECT -> {
                try {
                    var gameData = service.getGame(command.getGameID());
                    var username = service.validateToken(command.getAuthToken());
                    if (!observers.containsKey(command.getGameID())) {
                        observers.put(command.getGameID(), new ArrayList<>(Collections.singletonList(username)));
                    } else {
                        observers.get(command.getGameID()).add(username);
                    }
                    var message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "", gameData);
                    ctx.session.getRemote().sendString(gson.toJson(message));
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
            case MAKE_MOVE -> {
                try {
                    service.validateToken(command.getAuthToken());
                    service.makeMove(command.getGameID(), command.getMove());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                } catch (InvalidMoveException e) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid move");
                    ctx.session.getRemote().sendString(gson.toJson(message));
                }
            }
            case LEAVE -> {
                try {
                    service.validateToken(command.getAuthToken());
                } catch (UnauthorizedResponse response) {
                    var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid authtoken");
                }
            }
            case RESIGN -> {
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}