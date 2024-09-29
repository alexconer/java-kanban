package com.shishkin.tasktracker;

import com.shishkin.tasktracker.enums.TaskStates;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

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
    public void addSubtask(Epic epic, Subtask subtask) {
        subtask.setId(nextId++);
        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
        updateEpicState(epic);
    }

    // пересчет статуса эпика
    public void updateEpicState(Epic epic) {

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

    // получение всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        return allTasks;
    }

    // получение всех эпиков
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        allEpics.addAll(epics.values());
        return allEpics;
    }

    // получение всех подзадач эпика
    public ArrayList<Subtask> getAllSubtasks(Epic epic) {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();

        for (Integer subtaskId : epic.getSubtasksIds()) {
            allSubtasks.add(subtasks.get(subtaskId));
        };
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
    public void updateEpic(Epic epic, boolean clearSubtasks) {
        // если эпик обновляется без удаления подзадач (и актуальный список не заполнен)
        if (!clearSubtasks && epic.getSubtasksIds().isEmpty()) {
            Epic epicOld = getEpicById(epic.getId());
            epic.setSubtasks(epicOld.getSubtasksIds());
        }
        epics.put(epic.getId(), epic);
    }

    // обновление подзадачи эпика
    public void updateSubtask(Subtask subtask) {
        Subtask subtaskOld = getSubtaskById(subtask.getId());
        subtask.setEpicId(subtaskOld.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        // обновляем статус эпика
        updateEpicState(getEpicById(subtask.getEpicId()));
    }

    // удаление задачи по id
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    // удаление эпика по id
    public void deleteEpicById(int epicId) {
        // удаляем подзадачи
        Epic epic = getEpicById(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(epicId);
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

    // удаление всех подзадач эпика
    public void deleteAllSubtasks(Epic epic) {
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
        epic.removeAllSubtasks();
        // обновляем статус эпика
        updateEpicState(epic);
    }

}
