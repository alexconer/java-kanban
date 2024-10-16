package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    /**
     * добавление задачи
     */
    void addTask(Task task);

    /**
     * добавление эпика
     */
    void addEpic(Epic epic);

    /**
     * добавление подзадачи эпика
     */
    void addSubtask(Subtask subtask);

    /**
     * получение всех задач
     */
    ArrayList<Task> getAllTasks();

    /**
     * получение всех эпиков
     */
    ArrayList<Epic> getAllEpics();

    /**
     * получение всех подзадач
     */
    ArrayList<Subtask> getAllSubtasks();

    /**
     * получение всех подзадач эпика
     */
    ArrayList<Subtask> getSubtasks(int epicId);

    /**
     * получение задачи по id
     */
    Task getTaskById(int taskId);

    /**
     * получение эпика по id
     */
    Epic getEpicById(int epicId);

    /**
     * получение подзадачи эпика по id
     */
    Subtask getSubtaskById(int subtaskId);

    /**
     * обновление задачи
     */
    void updateTask(Task task);

    /**
     * обновление эпика
     */
    void updateEpic(Epic epic);

    /**
     * обновление подзадачи эпика
     */
    void updateSubtask(Subtask subtask);

    /**
     * удаление задачи по id
     */
    void deleteTaskById(int taskId);

    /**
     * удаление эпика по id
     */
    void deleteEpicById(int epicId);

    /**
     * удаление подзадачи эпика по id
     */
    void deleteSubtaskById(int subtaskId);

    /**
     * удаление всех задач
     */
    void deleteAllTasks();

    /**
     * удаление всех эпиков
     */
    void deleteAllEpics();

    /**
     * удаление всех подзадач
     */
    void deleteAllSubtasks();

    /**
     * возвращает историю задач
     */
    List<Task> getHistory();
}
