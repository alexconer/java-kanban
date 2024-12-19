package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.service.TaskManager;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    public PrioritizedTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public String get(String[] path, String message) {
        return toJson(getTaskManager().getPrioritizedTasks());
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
