package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;
import java.util.List;

public interface HistoryManager  {
    /**
     * Добавляет задачу в историю
     */
    void add(Task task);

    /**
     * возвращает историю задач
     */
    List<Task> getHistory();
}
