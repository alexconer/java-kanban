package com.shishkin.tasktracker.service;

public class Managers {

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(10);
    }
}
