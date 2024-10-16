package com.shishkin.tasktracker;

import com.shishkin.tasktracker.model.TaskStates;
import com.shishkin.tasktracker.model.Epic;
import com.shishkin.tasktracker.model.Subtask;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // создаем менеджер задач
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        System.out.println("---> Добавляем задачи");
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        // выводим список задач
        printTasks(inMemoryTaskManager);

        System.out.println("---> Добавляем эпики");
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addEpic(epic3);
        // выводим список эпиков
        printEpics(inMemoryTaskManager);

        System.out.println("---> Добавляем задачи в эпики");
        Subtask subtask11 = new Subtask(epic1.getId(),"Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask(epic1.getId(),"Подзадача 12", "Описание подзадачи 12");
        inMemoryTaskManager.addSubtask(subtask11);
        inMemoryTaskManager.addSubtask(subtask12);

        Subtask subtask21 = new Subtask(epic2.getId(),"Подзадача 21", "Описание подзадачи 21");
        inMemoryTaskManager.addSubtask(subtask21);

        Subtask subtask31 = new Subtask(epic3.getId(),"Подзадача 31", "Описание подзадачи 31");
        inMemoryTaskManager.addSubtask(subtask31);
        // выводим список подзадач
        printEpics(inMemoryTaskManager);

        System.out.println("--->  Обновляем задачу 2");
        task2 = new Task(task2.getId(), "Задача 2 (обновлено)", "Описание задачи 2", TaskStates.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task2);
        // выводим список задач
        printTasks(inMemoryTaskManager);

        System.out.println("---> Обновляем эпик 2 и 3");
        epic2 = new Epic(epic2.getId(),"Эпик 2 (обновлено)", "Описание эпика 2");
        inMemoryTaskManager.updateEpic(epic2);

        epic3 = new Epic(epic3.getId(), "Эпик 3 (обновлено)", "Описание эпика 3");
        inMemoryTaskManager.updateEpic(epic3);
        printEpics(inMemoryTaskManager);

        System.out.println("---> Обновляем подзадачу 11");
        subtask11 = new Subtask(subtask11.getId(), epic1.getId(), "Подзадача 11 (обновлено)", "Описание подзадачи 11", TaskStates.DONE);
        inMemoryTaskManager.updateSubtask(subtask11);
        printEpics(inMemoryTaskManager);

        System.out.println("---> Обновляем подзадачу 12");
        subtask12 = new Subtask(subtask12.getId(), epic1.getId(),"Подзадача 12 (обновлено)", "Описание подзадачи 12", TaskStates.DONE);
        inMemoryTaskManager.updateSubtask(subtask12);
        printEpics(inMemoryTaskManager);

        System.out.println("---> Добавляем подзадачу 13");
        Subtask subtask13 = new Subtask(epic1.getId(),"Подзадача 13", "Описание подзадачи 13");
        inMemoryTaskManager.addSubtask(subtask13);
        printEpics(inMemoryTaskManager);

        System.out.println("---> Удаляем подзадачу 13");
        inMemoryTaskManager.deleteSubtaskById(subtask13.getId());
        printEpics(inMemoryTaskManager);

        System.out.println("---> Удаляем эпик 2");
        inMemoryTaskManager.deleteEpicById(epic2.getId());
        printEpics(inMemoryTaskManager);

        System.out.println("---> Удаляем задачу 1");
        inMemoryTaskManager.deleteTaskById(task1.getId());
        printTasks(inMemoryTaskManager);

        System.out.println("---> Все подзадачи");
        printSubtasks(inMemoryTaskManager);

        System.out.println("---> Удаляем все подзадачи");
        inMemoryTaskManager.deleteAllSubtasks();
        printEpics(inMemoryTaskManager);

    }

    private static void printTasks(InMemoryTaskManager inMemoryTaskManager) {
        // выводим список задач
        for (Task task : inMemoryTaskManager.getAllTasks()) {
            System.out.println(task);
        }
    }

    private static void printEpics(InMemoryTaskManager inMemoryTaskManager) {
        // выводим список подзадач
        for (Epic epic : inMemoryTaskManager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : inMemoryTaskManager.getSubtasks(epic.getId())) {
                System.out.println(subtask);
            }
        }
    }

    private static void printSubtasks(InMemoryTaskManager inMemoryTaskManager) {
        // выводим список задач
        for (Subtask subtask : inMemoryTaskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }

}
