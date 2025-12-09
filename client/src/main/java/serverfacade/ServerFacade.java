package serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import data.GameData;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType.*;

import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.ArrayList;


public class ServerFacade implements ServerMessageHandler {
    private final String baseUrl;
    private final Gson gson = new Gson();
    private final Client client;
    private boolean isAuthorized;
    private WebSocketClient webSocketClient;
    private UpdateListener updateListener;

    public ServerFacade(String protocol, String host, int port, UpdateListener listener) throws Exception {
        baseUrl = String.format("%s://%s:%d", protocol, host, port);
        client = new Client();
        webSocketClient = new WebSocketClient(this, String.format("ws://%s:%d", host, port));
        updateListener = listener;
    }

    public boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void register(String username, String password, String email) throws Exception {
        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            throw new Client.BadRequestResponse();
        }
        String urlString = String.format("%s/user", baseUrl);
        HttpResponse<String> response = client.sendRequest(
                urlString,
                "POST",
                gson.toJson(
                        Map.of("username", username, "password", password, "email", email)));
        Map<String, String> responseBody = Client.receiveResponse(response);
        client.setAuthToken(responseBody.get("authToken"));
        isAuthorized = true;
    }

    public void login(String username, String password) throws Exception {
        if (username.isBlank() || password.isBlank()) {
            throw new Client.BadRequestResponse();
        }
        String urlString = String.format("%s/session", baseUrl);
        HttpResponse<String> response = client.sendRequest(
                urlString, "POST", gson.toJson(Map.of("username", username, "password", password)));
        Map<String, String> responseBody = Client.receiveResponse(response);
        client.setAuthToken(responseBody.get("authToken"));
        isAuthorized = true;
    }

    public void logout() throws Exception {
        String urlString = String.format("%s/session", baseUrl);
        client.sendRequest(urlString, "DELETE", null);
        client.setAuthToken(null);
        isAuthorized = false;
    }

    public int createGame(String gameName) throws Exception {
        if (gameName.isBlank()) {
            throw new Client.BadRequestResponse();
        }
        String urlString = String.format("%s/game", baseUrl);
        var response = client.sendRequest(
                urlString, "POST", gson.toJson(Map.of("gameName", gameName)));
        Map responseBody = Client.receiveResponse(response);
        return (int) Float.parseFloat(responseBody.get("gameID").toString());
    }

    public ArrayList<GameData> getGames() throws Exception {
        String urlString = String.format("%s/game", baseUrl);
        var response = client.sendRequest(
                urlString, "GET", null);
        Map responseBody = Client.receiveResponse(response);

        Type gamesType = new TypeToken<ArrayList<GameData>>() {
        }.getType();
        return gson.fromJson(responseBody.get("games").toString(), gamesType);
    }

    public void joinGame(int gameID, ChessGame.TeamColor color) throws Exception {
        String urlString = String.format("%s/game", baseUrl);
        client.sendRequest(
                urlString,
                "PUT",
                gson.toJson(
                        Map.of("gameID", gameID, "playerColor", color.toString())));
        UserGameCommand command = new UserGameCommand(CONNECT, client.getAuthToken(), gameID);
        webSocketClient.sendMessage(gson.toJson(command));
    }

    public void clear() throws Exception {
        String urlString = String.format("%s/db", baseUrl);
        client.sendRequest(urlString, "DELETE", null);
        client.setAuthToken(null);
    }

    public void makeMove(ChessMove move, int gameId) throws Exception {
        UserGameCommand command = new UserGameCommand(MAKE_MOVE, client.getAuthToken(), gameId, move);
        webSocketClient.sendMessage(gson.toJson(command));
    }

    public void leaveGame(int gameId) throws Exception {
        UserGameCommand command = new UserGameCommand(LEAVE, client.getAuthToken(), gameId);
        webSocketClient.sendMessage(gson.toJson(command));
    }

    public void resign(int gameId) throws Exception {
        UserGameCommand command = new UserGameCommand(RESIGN, client.getAuthToken(), gameId);
        webSocketClient.sendMessage(gson.toJson(command));
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (ServerMessageType.NOTIFICATION == message.getServerMessageType()) {
            updateListener.onNotification(message.getMessage());
        } else if (ServerMessageType.LOAD_GAME == message.getServerMessageType()) {
            updateListener.onLoadGame(message.getGame());
        } else {
            updateListener.onNotification(message.getErrorMessage());
        }
    }
}
