package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import data.GameData;
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
import static websocket.messages.ServerMessage.ServerMessageType.*;
import java.io.IOException;
import java.util.HashMap;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();
    private final Service service;
    private final HashMap<Integer, ConnectionManager> observers = new HashMap<>();

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

    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        ChessGame.TeamColor color = null;
        if (username.equals(gameData.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        }
        return color;
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

    private void sendNotification(int gameId, Session excludeSession, String message) throws IOException {
        var notification = new ServerMessage(NOTIFICATION, message, null, null);
        observers.get(gameId).broadcast(excludeSession, gson.toJson(notification));
    }

    private String assembleMovedString(String username, GameData gameData, ChessMove move) {
        var colorString = getPlayerColorString(username, gameData);
        return String.format(
                "%s as %s moved %s.",
                username,
                colorString,
                move.humanReadable());
    }

    private void handleEndGame(GameData gameData, String username, Session session) throws IOException {
        var gameId = gameData.gameID();
        var playerColor = getPlayerColor(username, gameData);
        ChessGame.TeamColor opponentColor = playerColor == ChessGame.TeamColor.WHITE
                ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        if (gameData.game().isInCheckmate(opponentColor)) {
            sendNotification(gameId, session, String.format("%s won", username));
            service.closeGame(gameId);
        } else if (gameData.game().isInStalemate(opponentColor)) {
            sendNotification(gameId, session, username + " put the game in stalemate");
            service.closeGame(gameId);
        }
    }

    private void handleGameConnection(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        addToObservers(ctx.session, gameId);

        try {
            var gameData = validateGameId(command);
            var username = validateAuthToken(command);

            var loadGameMessage = new ServerMessage(LOAD_GAME, null, null, gameData);
            ctx.session.getRemote().sendString(gson.toJson(loadGameMessage));

            sendNotification(gameId, ctx.session, String.format(
                    "%s has joined as %s.",
                    username,
                    getStatusString(username, gameData)));

        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, null, e.getMessage(), null)));
        }
    }

    private void handleMoveCommand(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();

        try {
            var gameData = validateGameId(command);
            var username = validateAuthToken(command);

            // Check if user can make move.
            ChessGame.TeamColor playerColor = getPlayerColor(username, gameData);
            if (playerColor == null) {
                throw new Exception("Observer cannot make moves");
            } else if (playerColor != gameData.game().getTeamTurn()) {
                throw new Exception("It's not your turn");
            }

            var move = command.getMove();
            var newGameData = service.makeMove(gameId, move);

            var loadGameMessage = new ServerMessage(LOAD_GAME, null, null, newGameData);
            observers.get(gameId).broadcast(null, gson.toJson(loadGameMessage));

            sendNotification(gameId, ctx.session, assembleMovedString(username, gameData, move));
            handleEndGame(newGameData, username, ctx.session);

        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, null, e.getMessage(), null)));
        }
    }

    private void handleLeaveCommand(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        try {
            removeFromObservers(ctx.session, gameId);
            var gameData = validateGameId(command);
            var username = validateAuthToken(command);
            if (username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername())) {
                service.leaveGame(gameId, username);
            }
            sendNotification(gameId, ctx.session, String.format("%s has left the game.", username));
        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, null, e.getMessage(), null)));
        }
    }

    private void handleResignCommand(WsMessageContext ctx, UserGameCommand command) throws IOException {
        var gameId = command.getGameID();
        try {
            var username = validateAuthToken(command);
            var gameData = validateGameId(command);
            if (gameData.isOver()) {
                throw new Exception("Game already over");
            } else if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                throw new Exception("Observer cannot resign");
            }
            service.closeGame(gameId);
            var notificationString = String.format("%s has resigned.", username);
            sendNotification(gameId, null, notificationString);
        } catch (Exception e) {
            ctx.session.getRemote().sendString(gson.toJson(
                    new ServerMessage(ERROR, null, e.getMessage(), null)));
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
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleGameConnection(ctx, command);
                case MAKE_MOVE -> handleMoveCommand(ctx, command);
                case LEAVE -> handleLeaveCommand(ctx, command);
                case RESIGN -> handleResignCommand(ctx, command);
            }
        } catch (Exception e) {
            var message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "A server error occurred");
            ctx.session.getRemote().sendString(gson.toJson(message));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}