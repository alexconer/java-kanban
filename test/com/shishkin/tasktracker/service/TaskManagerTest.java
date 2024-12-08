package com.shishkin.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;

public abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    abstract void setUp();
}
