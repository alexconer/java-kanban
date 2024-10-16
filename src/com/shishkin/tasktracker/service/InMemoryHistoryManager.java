package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY_SIZE = 10;

    private final List<Task> history;
    private final int size;

    public InMemoryHistoryManager() {
        this(MAX_HISTORY_SIZE);
    }

    public InMemoryHistoryManager(int size) {
        this.size = size;
        history = new ArrayList<>(size);
    }

    @Override
    public void add(Task task) {
        if (history.size() == this.size) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
