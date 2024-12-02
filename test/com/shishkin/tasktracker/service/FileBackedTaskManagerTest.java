package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {

    private TaskManager taskManager;
    private File file;

    @BeforeEach
    void beforeEach() throws IOException {
        file = new File("./resources/tasks_test.csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void saveAllTasksToFileTest() throws IOException {
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Epic1", "Desc1");
        Epic epic2 = new Epic("Epic2", "Desc2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask11 = new Subtask(epic1.getId(),"Subtask1", "Desc1");
        Subtask subtask12 = new Subtask(epic1.getId(),"Subtask2", "Desc2");
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        Subtask subtask21 = new Subtask(epic2.getId(),"Subtask2", "Desc1");
        taskManager.addSubtask(subtask21);

        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        while (br.ready()) {
            lines.add(br.readLine());
        }

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("id,type,name,status,description,epic");
        expectedLines.add("1,TASK,Task1,NEW,Desc1,");
        expectedLines.add("2,TASK,Task2,NEW,Desc2,");
        expectedLines.add("3,EPIC,Epic1,NEW,Desc1,");
        expectedLines.add("4,EPIC,Epic2,NEW,Desc2,");
        expectedLines.add("5,SUBTASK,Subtask1,NEW,Desc1,3");
        expectedLines.add("6,SUBTASK,Subtask2,NEW,Desc2,3");
        expectedLines.add("7,SUBTASK,Subtask2,NEW,Desc1,4");

        assertEquals(expectedLines, lines);
    }

    @Test
    void loadAllTasksFromFileTest() throws IOException {
        Task task1 = new Task("Task1", "Desc1");
        Task task2 = new Task("Task2", "Desc2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Epic1", "Desc1");
        Epic epic2 = new Epic("Epic2", "Desc2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        Subtask subtask11 = new Subtask(epic1.getId(),"Subtask1", "Desc1");
        Subtask subtask12 = new Subtask(epic1.getId(),"Subtask2", "Desc2");
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        Subtask subtask21 = new Subtask(epic2.getId(),"Subtask2", "Desc1");
        taskManager.addSubtask(subtask21);

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getAllTasks(), newTaskManager.getAllTasks());
        assertEquals(taskManager.getAllSubtasks(), newTaskManager.getAllSubtasks());
        assertEquals(taskManager.getAllEpics(), newTaskManager.getAllEpics());
    }

    @Test
    void saveEmptyManagerToFileTest() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        List<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        while (br.ready()) {
            lines.add(br.readLine());
        }

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add("id,type,name,status,description,epic");

        assertEquals(lines, expectedLines);
    }

    @Test
    void loadFromEmptyFileTest() throws IOException {
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
    }
}