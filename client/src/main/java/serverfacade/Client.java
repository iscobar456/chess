package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Client {
    private final HttpClient client = HttpClient.newHttpClient();
    private String authToken;

    public static class ResponseException extends Exception {};

    public static class BadRequestResponse extends ResponseException {};
    public static class UnauthorizedResponse extends ResponseException {};
    public static class ForbiddenResponse extends ResponseException {};
    public static class ServerErrorResponse extends ResponseException {};

    public HttpResponse<String> sendRequest(String url, String method, String body) throws Exception {
        var requestBuilder = HttpRequest.newBuilder(URI.create(url));
        requestBuilder.method(method, requestBodyPublisher(body));
        if (authToken != null) {
            requestBuilder.header("authorization", authToken);
        }
        var request = requestBuilder.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 400) {
            throw new BadRequestResponse();
        } else if (response.statusCode() == 401) {
            throw new UnauthorizedResponse();
        } else if (response.statusCode() == 403) {
            throw new ForbiddenResponse();
        } else if (response.statusCode() == 500) {
            throw new ServerErrorResponse();
        }
        return response;
    }

    private static HttpRequest.BodyPublisher requestBodyPublisher(String body) throws IOException {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public static Map receiveResponse(HttpResponse<String> response) {
        Map<String, String> responseBody = new Gson().fromJson(response.body(), Map.class);
        return responseBody;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
