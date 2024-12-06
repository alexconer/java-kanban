package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.exception.TaskIntersectionException;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void addDifferentTask() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(new Task("Задача 1", "Описание задачи 1"));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1)));

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12", LocalDateTime.of(2024,12,1,22,0), Duration.ofDays(1));
        Subtask subtask13 = new Subtask(epic1.getId(),"Подзадача 13", "Описание подзадачи 13", LocalDateTime.of(2024,12,2,22,0), Duration.ofDays(1));

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask13);

        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(3, taskManager.getAllSubtasks().size());
        assertEquals(3, taskManager.getPrioritizedTasks().size());
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
        Task task1 = new Task("Задача 1", "Описание задачи 1", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1));
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());

        taskManager.deleteTaskById(task1.getId());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task2.getId(), taskManager.getAllTasks().get(0).getId());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void deleteSubtask() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11", LocalDateTime.of(2024,12,3,22,0), Duration.ofDays(1));
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1));

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(2, epic1.getSubtasksIds().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());

        // удаляем подзадачу 11
        taskManager.deleteSubtaskById(subtask11.getId());

        assertEquals(1, taskManager.getAllSubtasks().size());
        assertEquals(1, epic1.getSubtasksIds().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void deleteEpic() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(), "Подзадача 11", "Описание подзадачи 11", LocalDateTime.of(2024,12,3,22,0), Duration.ofDays(1));
        Subtask subtask12 = new Subtask(epic1.getId(), "Подзадача 12", "Описание подзадачи 12", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1));

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);

        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(2, taskManager.getAllSubtasks().size());

        // удаляем эпик
        taskManager.deleteEpicById(epic1.getId());

        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());
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

    @Test
    void getPrioritizedTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        assertEquals(0, taskManager.getPrioritizedTasks().size());

        Task task2 = new Task("Задача 2", "Описание задачи 2", LocalDateTime.of(2024,12,4,22,0), Duration.ofDays(1));
        taskManager.addTask(task2);
        assertEquals(1, taskManager.getPrioritizedTasks().size());

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12", LocalDateTime.of(2024,12,3,22,0), Duration.ofDays(1));
        Subtask subtask13 = new Subtask(epic1.getId(),"Подзадача 13", "Описание подзадачи 13", LocalDateTime.of(2024,12,2,22,0), Duration.ofDays(1));
        Subtask subtask14 = new Subtask(epic1.getId(),"Подзадача 14", "Описание подзадачи 14", LocalDateTime.of(2024,12,1,22,0), Duration.ofDays(1));

        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask13);
        taskManager.addSubtask(subtask14);

        assertEquals(4, taskManager.getPrioritizedTasks().size());
        assertEquals(subtask14.getId(), taskManager.getPrioritizedTasks().get(0).getId());
        assertEquals(subtask13.getId(), taskManager.getPrioritizedTasks().get(1).getId());
        assertEquals(task2.getId(), taskManager.getPrioritizedTasks().getLast().getId());
        if (epic1.getDuration().isPresent()){
            assertEquals(4320, epic1.getDuration().get().toMinutes());
        }
    }

    @Test
    void intersectionTaskTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1", LocalDateTime.of(2024,12,4,22,0), Duration.ofHours(1));
        taskManager.addTask(task1);

        Task task4 = new Task("Задача 4", "Описание задачи 4", LocalDateTime.of(2024,12,4,21,30), Duration.ofHours(1));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task4));

        Task task5 = new Task("Задача 5", "Описание задачи 5", LocalDateTime.of(2024,12,4,22,0), Duration.ofHours(1));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task5));

        Task task6 = new Task("Задача 6", "Описание задачи 6", LocalDateTime.of(2024,12,4,22,30), Duration.ofMinutes(15));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task6));

        Task task7 = new Task("Задача 7", "Описание задачи 7", LocalDateTime.of(2024,12,4,22,30), Duration.ofMinutes(30));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task7));

        Task task8 = new Task("Задача 8", "Описание задачи 8", LocalDateTime.of(2024,12,4,22,30), Duration.ofHours(1));
        assertThrows(TaskIntersectionException.class, () -> taskManager.addTask(task8));

        Task task2 = new Task("Задача 2", "Описание задачи 2", LocalDateTime.of(2024,12,4,21,0), Duration.ofHours(1));
        assertDoesNotThrow(() -> taskManager.addTask(task2));

        Task task3 = new Task("Задача 3", "Описание задачи 3", LocalDateTime.of(2024,12,4,23,0), Duration.ofHours(1));
        assertDoesNotThrow(() -> taskManager.addTask(task3));
        assertEquals(3, taskManager.getPrioritizedTasks().size());

    }
}