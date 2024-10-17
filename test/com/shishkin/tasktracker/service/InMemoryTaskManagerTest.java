package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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

    @Test
    void addTaskInHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);

        // получаем задачу 2 по id
        taskManager.getTaskById(task2.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task2.getId(), taskManager.getHistory().get(0).getId());

        // получаем задачу 1 по id
        taskManager.getTaskById(task1.getId());
        assertEquals(2, taskManager.getHistory().size());
        assertEquals(task1.getId(), taskManager.getHistory().get(1).getId());
    }

    @Test
    void deleteTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);

        taskManager.deleteTaskById(task1.getId());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task2.getId(), taskManager.getAllTasks().get(0).getId());
    }

    @Test
    void deleteSubtask() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12");

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(2, epic1.getSubtasksIds().size());

        // удаляем подзадачу 11
        taskManager.deleteSubtaskById(subtask11.getId());

        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(1, epic1.getSubtasksIds().size());
    }

    @Test
    void deleteEpic() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(), "Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(), "Подзадача 12", "Описание подзадачи 12");

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(2, taskManager.getAllSubtasks().size());

        // удаляем эпик
        taskManager.deleteEpicById(epic1.getId());

        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void changeEpicStatus() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(), "Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(), "Подзадача 12", "Описание подзадачи 12");

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(TaskStates.NEW, epic1.getState());

        // обновляем подзадачу 11
        Subtask subtask11_new = new Subtask(subtask11.getId(), epic1.getId(), "Подзадача 11 (обновлено)", "Описание подзадачи 11", TaskStates.DONE);
        taskManager.updateSubtask(subtask11_new);

        assertEquals(TaskStates.IN_PROGRESS, epic1.getState());

        // обновляем подзадачу 12
        Subtask subtask12_new = new Subtask(subtask12.getId(), epic1.getId(), "Подзадача 12 (обновлено)", "Описание подзадачи 12", TaskStates.DONE);
        taskManager.updateSubtask(subtask12_new);

        assertEquals(TaskStates.DONE, epic1.getState());

        // удаляем подзадачи
        taskManager.deleteSubtaskById(subtask11.getId());
        taskManager.deleteSubtaskById(subtask12.getId());

        assertEquals(TaskStates.NEW, epic1.getState());
    }
}