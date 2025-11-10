package serverfacade;

import chess.ChessGame;
import com.google.gson.Gson;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.ArrayList;


public class ServerFacade {
    private final String baseUrl;
    private final Gson gson = new Gson();
    private final Client client;

    public ServerFacade(String protocol, String host, int port) {
        baseUrl = String.format("%s://%s:%d", protocol, host, port);
        client = new Client();
    }

    public void register(String username, String password, String email) throws Exception {
        String urlString = String.format("%s/user", baseUrl);
        HttpResponse<String> response = client.sendRequest(
                urlString,
                "POST",
                gson.toJson(
                        Map.of("username", username, "password", password, "email", email)));
        Map<String, String> responseBody = Client.receiveResponse(response);
        client.setAuthToken(responseBody.get("authToken"));
    }

    public void login(String username, String password) throws Exception {
        String urlString = String.format("%s/session", baseUrl);
        HttpResponse<String> response = client.sendRequest(
                urlString, "POST", gson.toJson(Map.of("username", username, "password", password)));
        Map<String, String> responseBody = Client.receiveResponse(response);
        client.setAuthToken(responseBody.get("authToken"));
    }

    public void logout() throws Exception {
        String urlString = String.format("%s/session", baseUrl);
        client.sendRequest(urlString, "DELETE", null);
        client.setAuthToken(null);
    }

    public int createGame(String gameName) throws Exception {
        String urlString = String.format("%s/game", baseUrl);
        var response = client.sendRequest(
                urlString, "POST", gson.toJson(Map.of("gameName", gameName)));
        Map responseBody = Client.receiveResponse(response);
        return (int) Float.parseFloat(responseBody.get("gameID").toString());
    }

    public ArrayList<GamesResponse.GameData> getGames() throws Exception {
        String urlString = String.format("%s/game", baseUrl);
        var response = client.sendRequest(
                urlString, "GET", null);
        Map responseBody = Client.receiveResponse(response);

        GamesResponse gamesResponse = gson.fromJson(responseBody.get("games").toString(), GamesResponse.class);
        return gamesResponse.getGames();
    }

    public void joinGame(int gameID, ChessGame.TeamColor color) throws Exception {
        String urlString = String.format("%s/game", baseUrl);
        client.sendRequest(
            urlString,
            "PUT",
            gson.toJson(
                Map.of("gameID", gameID, "playerColor", color.toString())));
    }

    public void clear() throws Exception {
        String urlString = String.format("%s/db", baseUrl);
        client.sendRequest(urlString, "DELETE", null);
        client.setAuthToken(null);
    }
}
