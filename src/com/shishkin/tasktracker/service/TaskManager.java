package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    private int nextId = 1;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    // добавление задачи
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    // добавление эпика
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    // добавление подзадачи эпика
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        updateEpicState(epic);
    }

    // получение всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // получение всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // получение всех подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // получение всех подзадач эпика
    public ArrayList<Subtask> getSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksIds()) {
            allSubtasks.add(subtasks.get(subtaskId));
        }
        return allSubtasks;
    }

    // получение задачи по id
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    // получение эпика по id
    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    // получение подзадачи эпика по id
    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    // обновление задачи
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // обновление эпика
    public void updateEpic(Epic epic) {
        final Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    // обновление подзадачи эпика
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        // обновляем статус эпика
        updateEpicState(epics.get(subtask.getEpicId()));
    }

    // удаление задачи по id
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    // удаление эпика по id
    public void deleteEpicById(int epicId) {
        // удаляем подзадачи
        final Epic epic = epics.remove(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
    }

    // удаление подзадачи эпика по id
    public void deleteSubtaskById(int subtaskId) {
        // удаляем подзадачу из эпика
        Subtask subtask = getSubtaskById(subtaskId);
        Epic epic = getEpicById(subtask.getEpicId());

        subtasks.remove(subtaskId);
        epic.removeSubtask(subtaskId);
        // обновляем статус эпика
        updateEpicState(epic);
    }

    // удаление всех задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // удаление всех эпиков
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // удаление всех подзадач
    public void deleteAllSubtasks() {
        // удаляем подзадачи из эпика
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
        }

        subtasks.clear();
    }

    // пересчет статуса эпика
    private void updateEpicState(Epic epic) {

        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;
        for (Integer subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = getSubtaskById(subtaskId);
            if (subtask.getState() != TaskStates.NEW) {
                allSubtasksNew = false;
            }
            if (subtask.getState() != TaskStates.DONE) {
                allSubtasksDone = false;
            }
            if (!allSubtasksDone && !allSubtasksNew){
                break;
            }
        }

        if (allSubtasksDone) {
            epic.setState(TaskStates.DONE);
        } else if (allSubtasksNew) {
            epic.setState(TaskStates.NEW);
        } else {
            epic.setState(TaskStates.IN_PROGRESS);
        }
    }

}
