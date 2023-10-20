package server;

import exception.KVClientException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class KVClient {
    private final String url;
    private final String apiToken;
    private final HttpClient httpClient;

    public KVClient(String url) {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
        apiToken = register();
    }

    private String register() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVClientException("Плохой ответ: не 200, а - " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получается сделать запрос метод register");
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                return null;
            }

            if (response.statusCode() != 200) {
                throw new KVClientException("Плохой ответ: не 200, а - " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получается сделать запрос");
        }
    }

    public void put(String key, String value) {
        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .build();
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new KVClientException("Ответ от сервера не 200, а - " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получилось сделать запрос метод Put");
        }
    }
}
