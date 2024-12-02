package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    private final HistoryManager historyManager;

    private int nextId = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
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
        updateEpicCondition(epic);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        List<Subtask> allSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksIds()) {
            allSubtasks.add(subtasks.get(subtaskId));
        }
        return allSubtasks;
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            historyManager.add(subtask);
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
        updateEpicCondition(epics.get(subtask.getEpicId()));
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
        // удаляем из истории
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        // удаляем подзадачи
        final Epic epic = epics.remove(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
            // удаляем из истории подзадачи
            historyManager.remove(subtaskId);
        }
        // удаляем из истории эпик
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        // удаляем подзадачу из эпика
        final Subtask subtask = subtasks.remove(subtaskId);
        final Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtaskId);
        // удаляем из истории подзадачи
        historyManager.remove(subtaskId);
        // обновляем статус эпика
        updateEpicCondition(epic);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        // удаляем из истории
        historyManager.remove(tasks.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        // удаляем из истории
        historyManager.remove(epics.values());
        historyManager.remove(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        // удаляем подзадачи из эпика
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            // обновляем статус эпика
            updateEpicCondition(epic);
        }

        subtasks.clear();
        // удаляем из истории
        historyManager.remove(subtasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // пересчет статуса эпика и его продолжительности
    private void updateEpicCondition(Epic epic) {
        // обновляем статус эпика
        Set<TaskStates> states = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getState())
                .collect(Collectors.toSet());

        if (states.size() == 1 && states.contains(TaskStates.DONE)) {
            epic.setState(TaskStates.DONE);// если все подзадачи выполнены, то статус эпика DONE
        } else if (states.isEmpty() || (states.size() == 1 && states.contains(TaskStates.NEW))) {
            epic.setState(TaskStates.NEW); // если все подзадачи новые, то статус эпика NEW
        } else {
            epic.setState(TaskStates.IN_PROGRESS);
        }

        // обновляем продолжительность эпика
        LocalDateTime startTime = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);

        LocalDateTime endTime = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getEndTime())
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);

        Duration duration = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getDuration())
                .filter(Objects::nonNull)
                .reduce(Duration::plus).orElse(null);

        if (startTime != null) {
            epic.setStartTimeAndDuration(startTime, duration);
            epic.setEndTime(endTime);
        }
    }
}
