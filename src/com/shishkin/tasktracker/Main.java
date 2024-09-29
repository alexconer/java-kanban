package com.shishkin.tasktracker;

import com.shishkin.tasktracker.enums.TaskStates;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // создаем менеджер задач
        TaskManager taskManager = new TaskManager();

        System.out.println("---> Добавляем задачи");
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        // выводим список задач
        printTasks(taskManager);

        System.out.println("---> Добавляем эпики");
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);
        // выводим список эпиков
        printSubtasks(taskManager);

        System.out.println("---> Добавляем задачи в эпики");
        Subtask subtask11 = new Subtask("Подзадача 11", "Описание подзадачи 11");
        Subtask subtask12 = new Subtask("Подзадача 12", "Описание подзадачи 12");
        taskManager.addSubtask(epic1, subtask11);
        taskManager.addSubtask(epic1, subtask12);

        Subtask subtask21 = new Subtask("Подзадача 21", "Описание подзадачи 21");
        taskManager.addSubtask(epic2, subtask21);

        Subtask subtask31 = new Subtask("Подзадача 31", "Описание подзадачи 31");
        taskManager.addSubtask(epic3, subtask31);
        // выводим список подзадач
        printSubtasks(taskManager);

        System.out.println("--->  Обновляем задачу 2");
        task2 = new Task(task2.getId(), "Задача 2 (обновлено)", "Описание задачи 2", TaskStates.IN_PROGRESS);
        taskManager.updateTask(task2);
        // выводим список задач
        printTasks(taskManager);

        System.out.println("---> Обновляем эпик 2 и 3");
        epic2 = new Epic(epic2.getId(),"Эпик 2 (обновлено)", "Описание эпика 2");
        taskManager.updateEpic(epic2, false);

        epic3 = new Epic(epic3.getId(), "Эпик 3 (обновлено)", "Описание эпика 3");
        taskManager.updateEpic(epic3, true);
        printSubtasks(taskManager);

        System.out.println("---> Обновляем подзадачу 11");
        subtask11 = new Subtask(subtask11.getId(), "Подзадача 11 (обновлено)", "Описание подзадачи 11", TaskStates.DONE);
        taskManager.updateSubtask(subtask11);
        printSubtasks(taskManager);

        System.out.println("---> Обновляем подзадачу 12");
        subtask12 = new Subtask(subtask12.getId(), "Подзадача 12 (обновлено)", "Описание подзадачи 12", TaskStates.DONE);
        taskManager.updateSubtask(subtask12);
        printSubtasks(taskManager);

        System.out.println("---> Добавляем подзадачу 13");
        Subtask subtask13 = new Subtask("Подзадача 13", "Описание подзадачи 13");
        taskManager.addSubtask(epic1, subtask13);
        printSubtasks(taskManager);

        System.out.println("---> Удаляем подзадачу 13");
        taskManager.deleteSubtaskById(subtask13.getId());
        printSubtasks(taskManager);

        System.out.println("---> Удаляем эпик 2");
        taskManager.deleteEpicById(epic2.getId());
        printSubtasks(taskManager);

        System.out.println("---> Удаляем задачу 1");
        taskManager.deleteTaskById(task1.getId());
        printTasks(taskManager);

    }

    private static void printTasks(TaskManager taskManager) {
        // выводим список задач
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
    }

    private static void printSubtasks(TaskManager taskManager) {
        // выводим список подзадач
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
            for (Subtask subtask : taskManager.getAllSubtasks(epic)) {
                System.out.println(subtask);
            }
        }
    }

}
