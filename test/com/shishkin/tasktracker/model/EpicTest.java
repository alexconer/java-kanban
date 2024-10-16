package com.shishkin.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic1;
    private Epic epic2;
    private Epic epic3;

    @BeforeEach
    public void setUp() {
        epic1 = new Epic(1, "epic1", "description1");
        epic2 = new Epic(1, "epic2", "description2");
        epic3 = new Epic(2, "epic1", "description1");
    }

    @Test
    public void epicWithSameIDIsEqual() {
        assertEquals(epic1, epic2);
    }

    @Test
    public void epicWithSameNameAndDifferentIDIsNotEqual() {
        assertNotEquals(epic1, epic3);
    }

}