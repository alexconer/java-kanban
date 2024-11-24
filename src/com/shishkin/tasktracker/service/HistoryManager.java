package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;

import java.util.Collection;
import java.util.List;

public interface HistoryManager  {
    /**
     * Добавляет задачу в историю
     */
    void add(Task task);

    /**
     * Удаляет задачу из истории по id
     */
    void remove(int id);

    /**
     * Удаляет задачу из истории по списку
     */
    void remove(Collection<? extends Task> elements);

    /**
     * возвращает историю задач
     */
    List<Task> getHistory();
}
