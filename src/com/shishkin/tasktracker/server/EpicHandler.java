package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.service.TaskManager;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String path, String message) {
        try {
            Object obj;
            if (path.matches("^/epics$")) {
                obj = getTaskManager().getAllEpics();
            } else if (path.matches("^/epics/\\d+$")) {
                obj = getTaskManager().getEpicById(getId(path));
            } else if (path.matches("^/epics/\\d+/subtasks$")) {
                obj = getTaskManager().getSubtasks(getId(path));
            } else {
                throw new NotFoundException("Некорректный запрос");
            }
            return toJson(obj);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид эпика");
        }
    }

    @Override
    public String post(String path, String message) {
        Epic epic = fromJson(message, Epic.class);

        if (epic.getId() == 0) {
            getTaskManager().addEpic(epic);
        } else {
            getTaskManager().updateEpic(epic);
        }
        return null;
    }

    @Override
    public String delete(String path, String message) {
        try {
            getTaskManager().deleteEpicById(getId(path));
            return null;
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид эпика");
        }
    }
}
