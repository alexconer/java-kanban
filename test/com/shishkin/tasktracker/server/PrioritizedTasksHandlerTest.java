package com.shishkin.tasktracker.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedTasksHandlerTest extends HttpHandlersTest {

    public PrioritizedTasksHandlerTest() throws IOException {
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        HttpResponse<String> response = getResponse("POST", "/tasks", gson.toJson(task1));
        assertEquals(201, response.statusCode());

        Task task2 = new Task("Задача 2", "Описание задачи 2", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1));
        response = getResponse("POST", "/tasks", gson.toJson(task2));
        assertEquals(201, response.statusCode());

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        response = getResponse("POST", "/epics", gson.toJson(epic1));
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        Epic loadedEpic = epicsFromManager.getFirst();

        Subtask subtask11 = new Subtask(loadedEpic.getId(),"Подзадача 11", "Описание подзадачи 11");
        response = getResponse("POST", "/subtasks", gson.toJson(subtask11));
        assertEquals(201, response.statusCode());

        Subtask subtask12 = new Subtask(loadedEpic.getId(),"Подзадача 12", "Описание подзадачи 12", LocalDateTime.of(2024,12,3,22,0), Duration.ofDays(1));
        response = getResponse("POST", "/subtasks", gson.toJson(subtask12));
        assertEquals(201, response.statusCode());

        Subtask subtask13 = new Subtask(loadedEpic.getId(),"Подзадача 13", "Описание подзадачи 13", LocalDateTime.of(2024,12,2,22,0), Duration.ofDays(1));
        response = getResponse("POST", "/subtasks", gson.toJson(subtask13));
        assertEquals(201, response.statusCode());

        Subtask subtask14 = new Subtask(loadedEpic.getId(),"Подзадача 14", "Описание подзадачи 14", LocalDateTime.of(2024,12,1,22,0), Duration.ofDays(1));
        response = getResponse("POST", "/subtasks", gson.toJson(subtask14));
        assertEquals(201, response.statusCode());

        response = getResponse("GET", "/prioritized", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> prioritized = element.getAsJsonArray().asList();
        assertEquals(4, prioritized.size());

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(subtasksFromManager.get(3).getId(), prioritized.get(0).getAsJsonObject().get("id").getAsInt());
        assertEquals(subtasksFromManager.get(2).getId(), prioritized.get(1).getAsJsonObject().get("id").getAsInt());
        assertEquals(subtasksFromManager.get(1).getId(), prioritized.get(2).getAsJsonObject().get("id").getAsInt());
        assertEquals(tasksFromManager.get(1).getId(), prioritized.getLast().getAsJsonObject().get("id").getAsInt());
    }
}
