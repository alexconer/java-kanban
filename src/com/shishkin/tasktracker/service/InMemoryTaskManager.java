package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.exception.TaskIntersectionException;
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

    private final Comparator<Task> comparator = (t1, t2) -> {
        int result = 0;
        if (t1.getStartTime().isPresent() && t2.getStartTime().isPresent()) {
            result = t1.getStartTime().get().compareTo(t2.getStartTime().get());
        }
        if (result == 0) {
            result = t1.getId() - t2.getId();
        }
        return result;
    };

    private final Set<Task> sortedTasks = new TreeSet<>(comparator);

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

        // проверяем пересечение и добавляем в отсортированный список
        if (task.getStartTime().isPresent()) {
            if (hasIntersection(task)) {
                throw new TaskIntersectionException("Задачи пересекаются по времени выполнения");
            }

            sortedTasks.add(task);
        }

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

        // проверяем пересечение и добавляем в отсортированный список
        if (subtask.getStartTime().isPresent()) {
            if (hasIntersection(subtask)) {
                throw new TaskIntersectionException("Задачи пересекаются по времени выполнения");
            }

            sortedTasks.add(subtask);
        }

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
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }

        return epic.getSubtasksIds()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask == null) {
            throw new NotFoundException("Задача не найдена");
        }

        // проверяем пересечение и обновляем в отсортированном списке
        if (task.getStartTime().isPresent()) {
            if (hasIntersection(task)) {
                throw new TaskIntersectionException("Задачи пересекаются по времени выполнения");
            }

            sortedTasks.add(task);
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic oldEpic = epics.get(epic.getId());
        if (oldEpic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }

        // проверяем пересечение и обновляем в отсортированном списке
        if (subtask.getStartTime().isPresent()) {
            if (hasIntersection(subtask)) {
                throw new TaskIntersectionException("Задачи пересекаются по времени выполнения");
            }

            sortedTasks.add(subtask);
        }

        subtasks.put(subtask.getId(), subtask);
        // обновляем статус эпика
        updateEpicCondition(epics.get(subtask.getEpicId()));
    }

    @Override
    public void deleteTaskById(int taskId) {
        final Task task = tasks.remove(taskId);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }

        // удаляем из истории
        historyManager.remove(taskId);

        // удаляем из отсортированного списка
        sortedTasks.remove(task);
    }

    @Override
    public void deleteEpicById(int epicId) {
        // удаляем подзадачи
        final Epic epic = epics.remove(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        for (Integer subtaskId : epic.getSubtasksIds()) {
            final Subtask subtask = subtasks.remove(subtaskId);
            // удаляем из истории подзадачи
            historyManager.remove(subtaskId);

            // удаляем из отсортированного списка
            sortedTasks.remove(subtask);
        }
        // удаляем из истории эпик
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        // удаляем подзадачу из эпика
        final Subtask subtask = subtasks.remove(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        final Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtaskId);
        // удаляем из истории подзадачи
        historyManager.remove(subtaskId);

        // удаляем из отсортированного списка
        sortedTasks.remove(subtask);

        // обновляем статус эпика
        updateEpicCondition(epic);
    }

    @Override
    public void deleteAllTasks() {
        // удаляем из истории
        historyManager.remove(tasks.values());

        // удаляем из отсортированного списка
        clearSortedTasks(tasks.values());

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        // удаляем из истории
        historyManager.remove(epics.values());
        historyManager.remove(subtasks.values());

        // удаляем из отсортированного списка
        clearSortedTasks(subtasks.values());

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {

        // удаляем из истории
        historyManager.remove(subtasks.values());

        // удаляем из отсортированного списка
        clearSortedTasks(subtasks.values());

        // удаляем подзадачи из эпика
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            // обновляем статус эпика
            updateEpicCondition(epic);
        }

        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // очищает список отсортированных задач
    private void clearSortedTasks(Collection<? extends Task> tasks) {
        sortedTasks.removeAll(
                tasks
                   .stream()
                   .filter(task -> task.getStartTime().isPresent())
                   .collect(Collectors.toSet())
        );
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
        Optional<LocalDateTime> startTime = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime().orElse(null))
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> endTime = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getEndTime().orElse(null))
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        Optional<Duration> duration = epic.getSubtasksIds()
                .stream()
                .map(subtaskId -> subtasks.get(subtaskId).getDuration().orElse(null))
                .filter(Objects::nonNull)
                .reduce(Duration::plus);

        if (startTime.isPresent()) {
            epic.setStartTimeAndDuration(startTime.get(), duration.orElse(null));
            epic.setEndTime(endTime.orElse(null));
        }
    }

    /**
     * Проверяет на пересечение времени выполнения задач
     */
    private boolean hasIntersection(Task task) {
        if (task.getStartTime().isEmpty() || task.getEndTime().isEmpty()) {
            return false;
        }

        return sortedTasks.stream()
                .filter(t -> t.getStartTime().isPresent() && t.getEndTime().isPresent())
                .filter(t -> t.getId() != task.getId()) // не проверяем задачу, если она уже в списке
                .anyMatch(t -> (t.getStartTime().get().isAfter(task.getStartTime().get()) && t.getStartTime().get().isBefore(task.getEndTime().get())) // пересечение слева
                        || (t.getEndTime().get().isAfter(task.getStartTime().get()) && t.getStartTime().get().isBefore(task.getEndTime().get()))); // пересечение справа
    }
}
