package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.exception.ManagerLoadException;
import com.shishkin.tasktracker.exception.ManagerSaveException;
import com.shishkin.tasktracker.exception.TaskFromStringException;
import com.shishkin.tasktracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    /**
     * Сохраняем все данные в файл
     */
    private void save() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            String header = String.join(",", "id", "type", "name", "status", "description", "epic", "start_time", "duration");
            writer.write(header);
            writer.write("\n");

            // сохраняем все задачи
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

            // сохраняем все эпики
            for (Task task : getAllEpics()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

            // сохраняем все подзадачи
            for (Task task : getAllSubtasks()) {
                writer.write(taskToString(task));
                writer.write("\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    /**
     * Загружаем данные из файла
     */
    private void load() {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader)) {

            // пропускаем заголовок
            bufferedReader.readLine();

            // читаем все строки
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    continue;
                }

                Task task = taskFromString(line);
                if (task instanceof Epic) {
                    addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    addSubtask((Subtask) task);
                } else {
                    addTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException(e.getMessage());
        }
    }

    /**
     * преобразует задачу в строку
     */
    private String taskToString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",");
        builder.append(task.getType()).append(",");
        builder.append(task.getName()).append(",");
        builder.append(task.getState()).append(",");
        builder.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            builder.append(((Subtask) task).getEpicId()).append(",");
        } else {
            builder.append(",");
        }
        if (task.getStartTime().isPresent()) {
            builder.append(task.getStartTime().get().format(DateTimeFormatter.ISO_DATE_TIME)).append(",");
        } else {
            builder.append(",");
        }
        if (task.getDuration().isPresent()) {
            builder.append(task.getDuration().get().toMinutes());
        }
        return builder.toString();
    }

    /**
     * преобразует строку в задачу
     */
    private Task taskFromString(String line) {
        String[] data = line.split(",");

        // получаем тип задачи
        TaskTypes type = TaskTypes.valueOf(data[1]);
        LocalDateTime startTime = data.length <= 6 ? null : LocalDateTime.parse(data[6], DateTimeFormatter.ISO_DATE_TIME);
        Duration duration = data.length <= 7 ? null : Duration.ofMinutes(Integer.parseInt(data[7]));

        // создаем задачу
        Task task;
        try {
            switch (type) {
                case TASK:
                    task = new Task(Integer.parseInt(data[0]), data[2], data[4], TaskStates.valueOf(data[3]), startTime, duration);
                    break;
                case EPIC:
                    task = new Epic(Integer.parseInt(data[0]), data[2], data[4]);
                    break;
                case SUBTASK:
                    task = new Subtask(Integer.parseInt(data[0]),Integer.parseInt(data[5]), data[2], data[4], TaskStates.valueOf(data[3]), startTime, duration);
                    break;
                default:
                    throw new TaskFromStringException("Неизвестный тип задачи: " + type);
            }
        } catch (Exception e) {
            throw new TaskFromStringException(e.getMessage());
        }
        return task;
    }

    /**
     * Восстанавливает данные из файла
     */
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

}
