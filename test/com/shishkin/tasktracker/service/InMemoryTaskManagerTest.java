package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void managersClassReturnDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void addDifferentTask() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(new Task("Задача 1", "Описание задачи 1"));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2"));

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12");
        Subtask subtask13 = new Subtask(epic1.getId(),"Подзадача 13", "Описание подзадачи 13");

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask13);

        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(3, taskManager.getAllSubtasks().size());
    }
}