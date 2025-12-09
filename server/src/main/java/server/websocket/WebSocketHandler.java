package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.*;

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

    private void removeFromObservers(Session session, int gameId) {
        if (!observers.containsKey(gameId)) {
            return;
        }
        observers.get(gameId).remove(session);
    }

    private String getPlayerColorString(String username, GameData gameData) {
        String colorString = null;
        if (username.equals(gameData.whiteUsername())) {
            colorString = "white";
        } else if (username.equals(gameData.blackUsername())) {
            colorString = "black";
        }
        return colorString;
    }

    private String getStatusString(String username, GameData gameData) {
        String colorString = getPlayerColorString(username, gameData);
        String statusString = "an observer";
        if (colorString != null) {
            statusString = colorString + " player";
        }
        return statusString;
    }

    private GameData validateGameId(UserGameCommand command) throws Exception {
        var gameId = command.getGameID();
        var gameData = service.getGame(gameId);
        if (gameData == null) {
            throw new Exception("Invalid game ID");
        }
        return gameData;
    }

    private String validateAuthToken(UserGameCommand command) throws Exception {
        var username = service.validateToken(command.getAuthToken());
        if (username == null) {
            throw new Exception("Invalid authtoken");
        }
        return username;
    }

    private void handleGameConnection(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        addToObservers(ctx.session, gameId);

        try {
            var gameData = validateGameId(command);
            var username = validateAuthToken(command);

            var loadGameMessage = new ServerMessage(LOAD_GAME, null, gameData);
            ctx.session.getRemote().sendString(gson.toJson(loadGameMessage));

            var notificationString = String.format(
                    "%s has joined as %s.", username, getStatusString(username, gameData));
            var connectedNotification = new ServerMessage(NOTIFICATION, notificationString, null);
            observers.get(gameId).broadcast(ctx.session, gson.toJson(connectedNotification));

        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, e.getMessage())));
        }
    }

    private void handleMoveCommand(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();

        try {
            var gameData = validateGameId(command);
            var username = validateAuthToken(command);
            var move = command.getMove();
            var newGameData = service.makeMove(gameId, move);

            var loadGameMessage = new ServerMessage(LOAD_GAME, null, newGameData);
            observers.get(gameId).broadcast(null, gson.toJson(loadGameMessage));

            var colorString = getPlayerColorString(username, gameData);
            var notification = String.format(
                    "%s moved %s to %s.",
                    colorString,
                    move.getStartPosition().toString(),
                    move.getEndPosition().toString());
            var notificationMessage = new ServerMessage(NOTIFICATION, notification, null);
            observers.get(gameId).broadcast(ctx.session, gson.toJson(notificationMessage));

        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, e.getMessage())));
        }
    }

    private void handleLeaveCommand(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        try {
            removeFromObservers(ctx.session, gameId);
            var username = validateAuthToken(command);
            var notification = String.format("%s has left the game.", username);
            var notificationMessage = new ServerMessage(NOTIFICATION, notification, null);
            observers.get(gameId).broadcast(ctx.session, gson.toJson(notificationMessage));
        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, e.getMessage())));
        }
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