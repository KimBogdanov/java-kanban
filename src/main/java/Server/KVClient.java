package Server;

import exception.KVClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

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
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new KVClientException("Плохой ответ: не 200, а - " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получается сделать запрос");
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получилось загрузить с KVServer");
        }
    }

    public void put(String key, String value) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 200) {
                System.out.println("Не удалось сохранить данные");
                throw new KVClientException("Ответ от сервера не 200, а - " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new KVClientException("Не получилось сделать запрос");
        }
    }
}
