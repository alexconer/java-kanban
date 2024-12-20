package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.service.TaskManager;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String path, String message) {
        try {
            Object obj;
            if (path.matches("^/subtasks$")) {
                obj = getTaskManager().getAllSubtasks();
            } else if (path.matches("^/subtasks/\\d+$")) {
                obj = getTaskManager().getSubtaskById(getId(path));
            } else {
                throw new NotFoundException("Некорректный запрос");
            }
            return toJson(obj);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид таска");
        }
    }

    @Override
    public String post(String path, String message) {
        Subtask subtask = fromJson(message, Subtask.class);

        int epicId = subtask.getEpicId();
        if (epicId == 0) {
            throw new NotFoundException("Некорректный ид эпика");
        }

        if (subtask.getId() == 0) {
            getTaskManager().addSubtask(subtask);
        } else {
            getTaskManager().updateSubtask(subtask);
        }
        return null;
    }

    @Override
    public String delete(String path, String message) {
        try {
            getTaskManager().deleteSubtaskById(getId(path));
            return null;
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид таска");
        }
    }
}
