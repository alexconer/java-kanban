package com.shishkin.tasktracker.server;

import com.google.gson.Gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import com.shishkin.tasktracker.service.Managers;
import com.shishkin.tasktracker.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {

    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = BaseHttpHandler.getGson();

    public TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task("Задача 1", "Описание задачи 1", now, Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Задача 1", tasksFromManager.getFirst().getName());
        assertEquals("Описание задачи 1", tasksFromManager.getFirst().getDescription());
        assertTrue(tasksFromManager.getFirst().getDuration().isPresent());
        assertTrue(tasksFromManager.getFirst().getStartTime().isPresent());
        assertEquals(now, tasksFromManager.getFirst().getStartTime().get());
        assertEquals(Duration.ofMinutes(5), tasksFromManager.getFirst().getDuration().get());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());

        // изменяем задачу
        int id = tasksFromManager.getFirst().getId();
        task = new Task(id, "Задача 2", "Описание задачи 2", TaskStates.IN_PROGRESS);
        taskJson = gson.toJson(task);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
        assertEquals("Задача 2", tasksFromManager.getFirst().getName());
        assertEquals("Описание задачи 2", tasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.IN_PROGRESS, tasksFromManager.getFirst().getState());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1");
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());

        // изменяем задачу
        int id = tasksFromManager.getFirst().getId();

        url = URI.create("http://localhost:8080/tasks/99");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/" + id);
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(0, tasksFromManager.size());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task("Задача 1", "Описание задачи 1", now, Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        task = new Task("Задача 2", "Описание задачи 2");
        taskJson = gson.toJson(task);

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(2, tasksFromManager.size());

        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> tasks = element.getAsJsonArray().asList();
        assertEquals(2, tasks.size());
        assertEquals(tasksFromManager.getFirst().getName(), tasks.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), tasks.getFirst().getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), tasks.getFirst().getAsJsonObject().get("state").getAsString());
        assertEquals(now.format(DateTimeFormatter.ISO_DATE_TIME), tasks.getFirst().getAsJsonObject().get("startTime").getAsString());
        assertEquals(5, tasks.getFirst().getAsJsonObject().get("duration").getAsInt());

        int id = tasksFromManager.getLast().getId();

        url = URI.create("http://localhost:8080/tasks/99");
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        url = URI.create("http://localhost:8080/tasks/" + id);
        request = HttpRequest.newBuilder().uri(url).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonObject());
        JsonElement taskEl = element.getAsJsonObject();
        assertEquals(tasksFromManager.getLast().getName(), taskEl.getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getLast().getDescription(), taskEl.getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), taskEl.getAsJsonObject().get("state").getAsString());
    }
}
