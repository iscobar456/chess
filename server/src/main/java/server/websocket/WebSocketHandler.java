package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import data.GameData;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
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
    private HashMap<Integer, ConnectionManager> observers = new HashMap<>();

    public WebSocketHandler(Service service) {
        this.service = service;
    }

    private void addToObservers(Session session, int gameId) {
        if (!observers.containsKey(gameId)) {
            observers.put(gameId, new ConnectionManager());
        }
        observers.get(gameId).add(session);
    }

    private String getStatusString(String username, GameData gameData) {
        String statusString;
        if (username.equals(gameData.whiteUsername())) {
            statusString = "white player";
        } else if (username.equals(gameData.blackUsername())) {
            statusString = "black player";
        } else {
            statusString = "an observer";
        }
        return statusString;
    }

    private void handleGameConnection(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        addToObservers(ctx.session, gameId);

        var gameData = service.getGame(gameId);
        var loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, gameData);
        ctx.session.getRemote().sendString(gson.toJson(loadGameMessage));

        var username = service.validateToken(command.getAuthToken());
        var connectedNotification = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s has joined as %s.", username, getStatusString(username, gameData)),
                null);
        observers.get(gameId).broadcast(ctx.session, gson.toJson(connectedNotification));
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
                    handleGameConnection(ctx, command);
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