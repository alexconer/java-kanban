package com.shishkin.tasktracker.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import com.shishkin.tasktracker.service.HistoryManager;
import com.shishkin.tasktracker.service.InMemoryHistoryManager;
import com.shishkin.tasktracker.service.Managers;
import com.shishkin.tasktracker.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends HttpHandlersTest{

    private Task task1;
    private Task task2;

    public HistoryHandlerTest() throws IOException {
    }

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        task1 = new Task("Задача 1", "Описание задачи 1", LocalDateTime.of(2024,12,4,22,0), Duration.ofHours(1));

        HttpResponse<String> response = getResponse("POST", "/tasks", gson.toJson(task1));
        assertEquals(201, response.statusCode());

        task2 = new Task("Задача 2", "Описание задачи 2", LocalDateTime.of(2024,12,4,23,0), Duration.ofHours(1));

        response = getResponse("POST", "/tasks", gson.toJson(task2));
        assertEquals(201, response.statusCode());
    }

    @Test
    void testHisrotyAdd() throws IOException, InterruptedException {
        List<Task> tasksFromManager = manager.getAllTasks();

        HttpResponse<String> response = getResponse("GET", "/tasks/" + tasksFromManager.get(1).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/history", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> history = element.getAsJsonArray().asList();
        assertEquals(1, history.size());
        assertEquals(tasksFromManager.get(1).getId(), history.getFirst().getAsJsonObject().get("id").getAsInt());
        assertEquals(tasksFromManager.get(1).getName(), history.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.get(1).getDescription(), history.getFirst().getAsJsonObject().get("description").getAsString());
    }

    @Test
    void testHistoryIsRewritable() throws IOException, InterruptedException {
        List<Task> tasksFromManager = manager.getAllTasks();

        HttpResponse<String> response = getResponse("GET", "/tasks/" + tasksFromManager.get(1).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/tasks/" + tasksFromManager.get(0).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/tasks/" + tasksFromManager.get(1).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/history", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> history = element.getAsJsonArray().asList();
        assertEquals(2, history.size());
        assertEquals(tasksFromManager.getFirst().getId(), history.getFirst().getAsJsonObject().get("id").getAsInt());
        assertEquals(tasksFromManager.getFirst().getName(), history.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), history.getFirst().getAsJsonObject().get("description").getAsString());
    }

    @Test
    void testHistoryAfterDelete() throws IOException, InterruptedException {
        List<Task> tasksFromManager = manager.getAllTasks();

        HttpResponse<String> response = getResponse("GET", "/tasks/" + tasksFromManager.get(0).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/tasks/" + tasksFromManager.get(1).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/history", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> history = element.getAsJsonArray().asList();
        assertEquals(2, history.size());

        // удаляем первую задачу
        response = getResponse("DELETE", "/tasks/" + tasksFromManager.get(0).getId(), null);
        assertEquals(200, response.statusCode());

        response = getResponse("GET", "/history", null);
        assertEquals(200, response.statusCode());

        element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        history = element.getAsJsonArray().asList();
        assertEquals(1, history.size());
        assertEquals(tasksFromManager.get(1).getId(), history.getFirst().getAsJsonObject().get("id").getAsInt());
        assertEquals(tasksFromManager.get(1).getName(), history.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.get(1).getDescription(), history.getFirst().getAsJsonObject().get("description").getAsString());

    }
}
