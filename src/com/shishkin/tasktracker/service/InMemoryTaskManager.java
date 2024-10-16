package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private final int MAX_HISTORY_SIZE = 10;

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final List<Task> history;

    private int nextId = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = new ArrayList<>(MAX_HISTORY_SIZE);
    }

    @Override
    public void addTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        updateEpicState(epic);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksIds()) {
            allSubtasks.add(subtasks.get(subtaskId));
        }
        return allSubtasks;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            addHistory(task);
        }

        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            addHistory(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            addHistory(subtask);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        // обновляем статус эпика
        updateEpicState(epics.get(subtask.getEpicId()));
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        // удаляем подзадачи
        final Epic epic = epics.remove(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        // удаляем подзадачу из эпика
        final Subtask subtask = subtasks.remove(subtaskId);
        final Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtaskId);
        // обновляем статус эпика
        updateEpicState(epic);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        // удаляем подзадачи из эпика
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            // обновляем статус эпика
            updateEpicState(epic);
        }

        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    // пересчет статуса эпика
    private void updateEpicState(Epic epic) {

        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;
        for (Integer subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);
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

        if (allSubtasksNew) {
            epic.setState(TaskStates.NEW);
        } else if (allSubtasksDone) {
            epic.setState(TaskStates.DONE);
        } else {
            epic.setState(TaskStates.IN_PROGRESS);
        }
    }

    // добавляет в историю
    private void addHistory(Task task) {
        if (history.size() == MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        history.add(task);
    }

}
