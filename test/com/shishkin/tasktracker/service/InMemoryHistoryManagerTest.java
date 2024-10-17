package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAdd() {
        HistoryManager historyManager = new InMemoryHistoryManager(10);
        historyManager.add(new Task("task1", "description1"));
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void testAddWhenHistoryIsFull() {
        HistoryManager historyManager = new InMemoryHistoryManager(2);
        Task task1 = new Task("task1", "description1");
        historyManager.add(task1);
        Task task2 = new Task("task2", "description2");
        historyManager.add(task2);
        Task task3 = new Task("task3", "description3");
        historyManager.add(task3);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }

}