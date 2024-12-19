package com.shishkin.tasktracker.server;

import com.google.gson.Gson;
import com.shishkin.tasktracker.service.Managers;
import com.shishkin.tasktracker.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHandlersTest {
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = BaseHttpHandler.getGson();

    public HttpHandlersTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    public HttpResponse<String> getResponse(String method, String urlPart, String body) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080" + urlPart);
        HttpRequest.BodyPublisher publisher = (body == null) ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body);
        HttpRequest request = HttpRequest.newBuilder().uri(url).method(method, publisher).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
