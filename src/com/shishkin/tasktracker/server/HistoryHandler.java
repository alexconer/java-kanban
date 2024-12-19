package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.service.TaskManager;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String[] path, String message) {
        return toJson(getTaskManager().getHistory());
    }

    @Override
    public String post(String[] path, String message) {
        throw new NotFoundException("not implemented");
    }

    @Override
    public String delete(String[] path, String message) {
        throw new NotFoundException("not implemented");
    }
}
