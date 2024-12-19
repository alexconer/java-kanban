package com.shishkin.tasktracker.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest extends HttpHandlersTest {

    private Task task1;
    private Task task2;

    public TaskHandlerTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        // проверяем, что создалась задача
        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(2, tasksFromManager.size());
        assertEquals(task1.getName(), tasksFromManager.getFirst().getName());
        assertEquals(task1.getDescription(), tasksFromManager.getFirst().getDescription());
        assertTrue(tasksFromManager.getFirst().getDuration().isPresent());
        assertTrue(tasksFromManager.getFirst().getStartTime().isPresent());
        if (task1.getStartTime().isPresent()) {
            assertEquals(task1.getStartTime().get(), tasksFromManager.getFirst().getStartTime().get());
        }
        if (task1.getDuration().isPresent()) {
            assertEquals(task1.getDuration().get(), tasksFromManager.getFirst().getDuration().get());
        }
    }

    @Test
    public void testAddIntersectionTask() throws IOException, InterruptedException {
        Task task3 = new Task("Задача 3", "Описание задачи 3", LocalDateTime.of(2024,12,4,23,30), Duration.ofHours(1));

        HttpResponse<String> response = getResponse("POST", "/tasks", gson.toJson(task3));
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(2, tasksFromManager.size());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        List<Task> tasksFromManager = manager.getAllTasks();
        assertEquals(2, tasksFromManager.size());

        // изменяем задачу
        int id = tasksFromManager.getFirst().getId();
        Task newTask = new Task(id, "Задача 1 (update)", "Описание задачи 1", TaskStates.IN_PROGRESS);
        HttpResponse<String> response = getResponse("POST", "/tasks", gson.toJson(newTask));

        assertEquals(201, response.statusCode());

        tasksFromManager = manager.getAllTasks();

        assertEquals(2, tasksFromManager.size());
        assertEquals(newTask.getName(), tasksFromManager.getFirst().getName());
        assertEquals(newTask.getDescription(), tasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.IN_PROGRESS, tasksFromManager.getFirst().getState());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager);
        assertEquals(2, tasksFromManager.size());

        int id = tasksFromManager.getFirst().getId();
        HttpResponse<String> response = getResponse("DELETE", "/tasks/99999", null);
        assertEquals(404, response.statusCode());

        response = getResponse("DELETE", "/tasks/" + id, null);
        assertEquals(200, response.statusCode());

        tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager);
        assertEquals(1, tasksFromManager.size());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(2, tasksFromManager.size());

        HttpResponse<String> response = getResponse("GET", "/tasks", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> tasks = element.getAsJsonArray().asList();
        assertEquals(2, tasks.size());
        assertEquals(tasksFromManager.getFirst().getName(), tasks.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), tasks.getFirst().getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), tasks.getFirst().getAsJsonObject().get("state").getAsString());
        if (tasksFromManager.getFirst().getStartTime().isPresent()) {
            assertEquals(tasksFromManager.getFirst().getStartTime().get().format(DateTimeFormatter.ISO_DATE_TIME), tasks.getFirst().getAsJsonObject().get("startTime").getAsString());
        }
        if (tasksFromManager.getFirst().getDuration().isPresent()) {
            assertEquals(tasksFromManager.getFirst().getDuration().get().toMinutes(), tasks.getFirst().getAsJsonObject().get("duration").getAsInt());
        }

        int id = tasksFromManager.getLast().getId();

        response = getResponse("GET", "/tasks/9999", null);
        assertEquals(404, response.statusCode());

        response = getResponse("GET", "/tasks/" + id, null);
        assertEquals(200, response.statusCode());

        element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonObject());
        JsonElement taskEl = element.getAsJsonObject();
        assertEquals(tasksFromManager.getLast().getName(), taskEl.getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getLast().getDescription(), taskEl.getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), taskEl.getAsJsonObject().get("state").getAsString());
    }
}
