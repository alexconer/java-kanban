package com.shishkin.tasktracker.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void managersClassReturnDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }
}