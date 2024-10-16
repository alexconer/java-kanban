package com.shishkin.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    @BeforeEach
    public void setUp() {
        subtask1 = new Subtask(1, 1, "subtask1", "description1", TaskStates.NEW);
        subtask2 = new Subtask(1, 1, "subtask2", "description2", TaskStates.NEW);
        subtask3 = new Subtask(2, 1, "subtask1", "description1", TaskStates.NEW);
    }

    @Test
    public void SubtaskWithSameIDIsEqual() {
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void SubtaskWithSameNameAndDifferentIDIsNotEqual() {
        assertNotEquals(subtask1, subtask3);
    }
}