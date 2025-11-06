package serverfacade;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Client {
    private static final HttpClient client = HttpClient.newHttpClient();

    private static HttpResponse<String> sendRequest(String url, String method, String body)
            throws InterruptedException, IOException {
        var request = HttpRequest.newBuilder(URI.create(url))
                .method(method, requestBodyPublisher(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpRequest.BodyPublisher requestBodyPublisher(String body) throws IOException {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private static void receiveResponse(HttpResponse<String> response) {
        var statusCode = response.statusCode();

        var responseBody = new Gson().fromJson(response.body(), Map.class);
        System.out.printf("= Response =========\n[%d]\n\n%s\n\n", statusCode, responseBody);
    }
}
