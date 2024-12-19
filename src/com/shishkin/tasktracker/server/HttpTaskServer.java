package com.shishkin.tasktracker.server;

import com.shishkin.tasktracker.service.FileBackedTaskManager;
import com.shishkin.tasktracker.service.TaskManager;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;

public class HttpTaskServer {

    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
    }

    public void start() throws IOException {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(FileBackedTaskManager.loadFromFile(new File("./resources/tasks.csv")));
        server.start();
    }
}
