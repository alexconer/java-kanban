package com.shishkin.tasktracker.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerTest extends HttpHandlersTest {

    private Epic epic1;
    private Subtask subtask11;
    private Subtask subtask12;

    public SubtaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        epic1 = new Epic("Эпик 1", "Описание эпика 1");

        HttpResponse<String> response = getResponse("POST", "/epics", gson.toJson(epic1));
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        Epic loadedEpic = epicsFromManager.getFirst();

        assertEquals(1, epicsFromManager.size());
        assertEquals(TaskStates.NEW, loadedEpic.getState());

        LocalDateTime startTime11 = LocalDateTime.of(2024,12,1,22,0);
        subtask11 = new Subtask(loadedEpic.getId(), "Подзадача 11", "Описание подзадачи 11", startTime11, Duration.ofHours(1));
        response = getResponse("POST", "/subtasks", gson.toJson(subtask11));
        assertEquals(201, response.statusCode());

        LocalDateTime startTime12 = LocalDateTime.of(2024,12,2,22,0);
        subtask12 = new Subtask(loadedEpic.getId(), "Подзадача 12", "Описание подзадачи 12", startTime12, Duration.ofHours(1));
        response = getResponse("POST", "/subtasks", gson.toJson(subtask12));
        assertEquals(201, response.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());
        assertEquals(subtask11.getName(), subtasksFromManager.getFirst().getName());
        assertEquals(subtask11.getDescription(), subtasksFromManager.getFirst().getDescription());
        assertTrue(subtasksFromManager.getFirst().getDuration().isPresent());
        assertTrue(subtasksFromManager.getFirst().getStartTime().isPresent());
        assertEquals(subtasksFromManager.getFirst().getStartTime().get(), subtasksFromManager.getFirst().getStartTime().get());
        assertEquals(Duration.ofHours(1), subtasksFromManager.getFirst().getDuration().get());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(2, manager.getSubtasks(epicsFromManager.getFirst().getId()).size());
    }

    @Test
    public void testAddIntersectionSubtask() throws IOException, InterruptedException {

        List<Epic> epicsFromManager = manager.getAllEpics();

        LocalDateTime startTime2 = LocalDateTime.of(2024,12,2,22,30);
        Subtask subtask2 = new Subtask(epicsFromManager.getFirst().getId(),"Подзадача 2", "Описание подзадачи 2", startTime2, Duration.ofMinutes(5));

        HttpResponse<String> response = getResponse("POST", "/subtasks", gson.toJson(subtask2));
        assertEquals(406, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();
        assertEquals(2, tasksFromManager.size());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        List<Epic> epicsFromManager = manager.getAllEpics();
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());

        // изменяем задачу
        int id = subtasksFromManager.getFirst().getId();
        subtask11 = new Subtask(id, epicsFromManager.getFirst().getId(), "Подзадача 1 (update)", "Описание подзадачи 1", TaskStates.IN_PROGRESS);
        HttpResponse<String> response = getResponse("POST", "/subtasks", gson.toJson(subtask11));

        assertEquals(201, response.statusCode());

        subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());
        assertEquals(subtask11.getName(), subtasksFromManager.getFirst().getName());
        assertEquals(subtask11.getDescription(), subtasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.IN_PROGRESS, subtasksFromManager.getFirst().getState());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());

        int id = subtasksFromManager.getFirst().getId();

        HttpResponse<String> response = getResponse("DELETE", "/subtasks/99999", null);

        assertEquals(404, response.statusCode());

        response = getResponse("DELETE", "/subtasks/" + id, null);

        assertEquals(200, response.statusCode());

        subtasksFromManager = manager.getAllSubtasks();

        assertEquals(1, subtasksFromManager.size());
        assertEquals(subtask12.getName(), subtasksFromManager.getFirst().getName());
        assertEquals(subtask12.getDescription(), subtasksFromManager.getFirst().getDescription());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        List<Epic> epicsFromManager = manager.getAllEpics();
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());

        HttpResponse<String> response = getResponse("GET", "/subtasks", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> tasks = element.getAsJsonArray().asList();
        assertEquals(2, tasks.size());
        assertEquals(subtasksFromManager.getFirst().getName(), tasks.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(subtasksFromManager.getFirst().getDescription(), tasks.getFirst().getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), tasks.getFirst().getAsJsonObject().get("state").getAsString());
        if (subtasksFromManager.getFirst().getStartTime().isPresent()) {
            assertEquals(subtasksFromManager.getFirst().getStartTime().get().format(DateTimeFormatter.ISO_DATE_TIME), tasks.getFirst().getAsJsonObject().get("startTime").getAsString());
        }
        if (subtasksFromManager.getFirst().getDuration().isPresent()) {
            assertEquals(subtasksFromManager.getFirst().getDuration().get().toMinutes(), tasks.getFirst().getAsJsonObject().get("duration").getAsInt());
        }

        int id = subtasksFromManager.getLast().getId();

        response = getResponse("GET", "/subtasks/9999", null);
        assertEquals(404, response.statusCode());

        response = getResponse("GET", "/subtasks/" + id, null);
        assertEquals(200, response.statusCode());

        element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonObject());
        JsonElement taskEl = element.getAsJsonObject();
        assertEquals(subtasksFromManager.getLast().getName(), taskEl.getAsJsonObject().get("name").getAsString());
        assertEquals(subtasksFromManager.getLast().getDescription(), taskEl.getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), taskEl.getAsJsonObject().get("state").getAsString());
    }
}
