package serverfacade;

import com.google.gson.Gson;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private String authToken;
    private String baseUrl;
    private Gson gson = new Gson();

    public ServerFacade(String protocol, String host, int port) {
        baseUrl = String.format("%s://%s:%d", protocol, host, port);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void register(String username, String password, String email) {

    }

    public String login(String username, String password) {
        try {
            String urlString = String.format("%s/login", baseUrl);
            HttpResponse<String> response = Client.sendRequest(urlString, "GET", gson.toJson(Map.of("username", username, "password", password)));
            Map<String, String> responseBody = Client.receiveResponse(response);
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    public void logout() {

    }

    public void createGame(String gameName) {

    }

    public void listGames() {

    }

    public void joinGame(int gameID) {

    }

    private record LoginRequest(String username, String password) {
    }

    ;

    private record RegisterRequest(String username, String password, String email) {
    }

    ;

    private record CreateGameRequest(String gameName) {
    }

    ;

    private record JoinGameRequest(int gameID) {
    }

    ;
}
