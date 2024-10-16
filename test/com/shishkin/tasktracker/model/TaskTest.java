package com.shishkin.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        task1 = new Task(1, "task1", "description1", TaskStates.NEW);
        task2 = new Task(1, "task2", "description2", TaskStates.NEW);
        task3 = new Task(2, "task1", "description1", TaskStates.NEW);
    }

    @Test
    public void TaskWithSameIDIsEqual() {
        assertEquals(task1, task2);
    }

    @Test
    public void TaskWithSameNameAndDifferentIDIsNotEqual() {
        assertNotEquals(task1, task3);
    }
}