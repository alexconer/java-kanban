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

public class EpicHandlerTest extends HttpHandlersTest {

    Epic epic1;
    Subtask subtask11;
    Subtask subtask12;

    public EpicHandlerTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(1, tasksFromManager.size());
        assertEquals(epic1.getName(), tasksFromManager.getFirst().getName());
        assertEquals(epic1.getDescription(), tasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.NEW, tasksFromManager.getFirst().getState());
    }

    @Test
    public void testChangeEpicStatus() throws IOException, InterruptedException {

        List<Epic> epicsFromManager = manager.getAllEpics();
        Epic loadedEpic = epicsFromManager.getFirst();

        // обновляем подзадачу 11
        List<Subtask> subtasks = manager.getSubtasks(loadedEpic.getId());
        Subtask subtask11_new = new Subtask(subtasks.get(0).getId(), loadedEpic.getId(), "Подзадача 11 (обновлено)", "Описание подзадачи 11", TaskStates.DONE);
        HttpResponse<String> response = getResponse("POST", "/subtasks", gson.toJson(subtask11_new));
        assertEquals(201, response.statusCode());

        assertEquals(TaskStates.IN_PROGRESS, loadedEpic.getState());

        // обновляем подзадачу 12
        Subtask subtask12_new = new Subtask(subtasks.get(1).getId(), loadedEpic.getId(), "Подзадача 12 (обновлено)", "Описание подзадачи 12", TaskStates.DONE);
        response = getResponse("POST", "/subtasks", gson.toJson(subtask12_new));
        assertEquals(201, response.statusCode());

        assertEquals(TaskStates.DONE, loadedEpic.getState());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(1, tasksFromManager.size());
        assertEquals(epic1.getName(), tasksFromManager.getFirst().getName());
        assertEquals(epic1.getDescription(), tasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.NEW, tasksFromManager.getFirst().getState());

        // изменяем эпик
        int id = tasksFromManager.getFirst().getId();
        epic1 = new Epic(id, "Эпик 2", "Описание эпика 2");
        HttpResponse<String> response = getResponse("POST", "/epics", gson.toJson(epic1));

        assertEquals(201, response.statusCode());

        assertEquals(1, tasksFromManager.size());
        assertEquals(epic1.getName(), tasksFromManager.getFirst().getName());
        assertEquals(epic1.getDescription(), tasksFromManager.getFirst().getDescription());
        assertEquals(TaskStates.NEW, tasksFromManager.getFirst().getState());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        List<Epic> epicsFromManager = manager.getAllEpics();
        Epic loadedEpic = epicsFromManager.getFirst();

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(2, subtasksFromManager.size());

        HttpResponse<String> response = getResponse("DELETE", "/epics/99999", null);
        assertEquals(404, response.statusCode());

        response = getResponse("DELETE", "/epics/" + loadedEpic.getId(), null);
        assertEquals(200, response.statusCode());

        epicsFromManager = manager.getAllEpics();
        assertEquals(0, epicsFromManager.size());

        subtasksFromManager = manager.getAllSubtasks();
        assertEquals(0, subtasksFromManager.size());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        HttpResponse<String> response = getResponse("POST", "/epics", gson.toJson(epic2));
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertEquals(2, tasksFromManager.size());

        response = getResponse("GET", "/epics", null);
        assertEquals(200, response.statusCode());

        JsonElement element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonArray());
        List<JsonElement> epics = element.getAsJsonArray().asList();
        assertEquals(2, epics.size());
        assertEquals(tasksFromManager.getFirst().getName(), epics.getFirst().getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getFirst().getDescription(), epics.getFirst().getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), epics.getFirst().getAsJsonObject().get("state").getAsString());
        assertEquals(LocalDateTime.of(2024,12,2,23,0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), epics.getFirst().getAsJsonObject().get("endTime").getAsString());
        assertEquals(120, epics.getFirst().getAsJsonObject().get("duration").getAsInt());

        int id = tasksFromManager.getLast().getId();

        response = getResponse("GET", "/epics/9999", null);
        assertEquals(404, response.statusCode());

        response = getResponse("GET", "/epics/" + id, null);
        assertEquals(200, response.statusCode());

        element = JsonParser.parseString(response.body());
        assertTrue(element.isJsonObject());
        JsonElement taskEl = element.getAsJsonObject();
        assertEquals(tasksFromManager.getLast().getName(), taskEl.getAsJsonObject().get("name").getAsString());
        assertEquals(tasksFromManager.getLast().getDescription(), taskEl.getAsJsonObject().get("description").getAsString());
        assertEquals(TaskStates.NEW.toString(), taskEl.getAsJsonObject().get("state").getAsString());
    }
}
