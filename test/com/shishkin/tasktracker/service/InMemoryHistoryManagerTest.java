package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAdd() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(new Task("task1", "description1"));
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void testHistoryIsRewritable() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        // добавляем три задачи
        Task task1 = new Task(1, "task1", "description1", TaskStates.NEW);
        historyManager.add(task1);
        Task task2 = new Task(2, "task2", "description2", TaskStates.IN_PROGRESS);
        historyManager.add(task2);
        historyManager.add(task1);

        // проверяем что размер хранилища = 2
        assertEquals(2, historyManager.getHistory().size());
        // проверяем что задача 1 исключена, остались задачи 2 и 3
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task1, historyManager.getHistory().get(1));
    }

    @Test
    void testHistoryAfterDelete() {
        TaskManager taskManager = Managers.getDefault();
        // добавляем две задачи
        Task task1 = new Task(1, "task1", "description1", TaskStates.NEW);
        taskManager.addTask(task1);
        taskManager.getTaskById(task1.getId());
        Task task2 = new Task(2, "task2", "description2", TaskStates.IN_PROGRESS);
        taskManager.addTask(task2);
        taskManager.getTaskById(task2.getId());

        // проверяем что размер хранилища = 2
        assertEquals(2, taskManager.getHistory().size());

        // удаляем первую задачу
        taskManager.deleteTaskById(task1.getId());
        // проверяем что размер хранилища = 1
        assertEquals(1, taskManager.getHistory().size());
        // проверяем что в хранилище осталась только вторая задача
        assertEquals(task2, taskManager.getHistory().get(0));
    }

}