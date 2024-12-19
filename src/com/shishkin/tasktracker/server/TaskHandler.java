package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.service.TaskManager;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String[] path, String message) {

        try {
            Object obj = switch (path.length) {
                case 2 -> getTaskManager().getAllTasks();
                case 3 -> getTaskManager().getTaskById(Integer.parseInt(path[2]));
                default -> throw new NotFoundException("Некорректный запрос");
            };
            return toJson(obj);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид задачи");
        }
    }

    @Override
    public String post(String[] path, String message) {

        Task task = fromJson(message, Task.class);

        if (task.getId() == 0) {
            getTaskManager().addTask(task);
        } else {
            getTaskManager().updateTask(task);
        }
        return null;
    }

    @Override
    public String delete(String[] path, String message) {
        try {
            getTaskManager().deleteTaskById(Integer.parseInt(path[2]));
            return null;
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид задачи");
        }
    }

}
