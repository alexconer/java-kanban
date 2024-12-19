package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.service.TaskManager;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String[] path, String message) {
        try {
            Object obj = switch (path.length) {
                case 2 -> getTaskManager().getAllEpics();
                case 3 -> getTaskManager().getEpicById(Integer.parseInt(path[2]));
                case 4 -> getTaskManager().getSubtasks(Integer.parseInt(path[2]));
                default -> throw new NotFoundException("Некорректный запрос");
            };
            return toJson(obj);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид эпика");
        }
    }

    @Override
    public String post(String[] path, String message) {
        Epic epic = fromJson(message, Epic.class);

        if (epic.getId() == 0) {
            getTaskManager().addEpic(epic);
        } else {
            getTaskManager().updateEpic(epic);
        }
        return null;
    }

    @Override
    public String delete(String[] path, String message) {
        try {
            getTaskManager().deleteEpicById(Integer.parseInt(path[2]));
            return null;
        } catch (NumberFormatException e) {
            throw new NotFoundException("Некорректный ид эпика");
        }
    }
}
