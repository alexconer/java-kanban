package com.shishkin.tasktracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shishkin.tasktracker.exception.NotFoundException;
import com.shishkin.tasktracker.exception.TaskFromStringException;
import com.shishkin.tasktracker.exception.TaskIntersectionException;
import com.shishkin.tasktracker.model.Task;
import com.shishkin.tasktracker.model.TaskStates;
import com.shishkin.tasktracker.server.adapters.DurationTypeAdapter;
import com.shishkin.tasktracker.server.adapters.LocalDateTimeAdapter;
import com.shishkin.tasktracker.service.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
                                .serializeNulls()
                                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public static Gson getGson() {
        return gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String requestMessage = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        String[] path = exchange.getRequestURI().getPath().split("/");
        String requestMethod = exchange.getRequestMethod();

        try (OutputStream responseBody = exchange.getResponseBody()) {
            int statusCode = 200;
            String respMessage = switch (requestMethod) {
                case "GET" -> get(path, requestMessage);
                case "POST" -> {statusCode = 201; yield post(path, requestMessage);}
                case "DELETE" -> delete(path, requestMessage);
                default -> throw new NotFoundException("Запрос не найден");
            };

            // если есть тело ответа отправляем его иначе просто статус
            if (respMessage != null && !respMessage.isEmpty()) {
                byte[] resp = respMessage.getBytes();
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(statusCode, resp.length);
                responseBody.write(resp);
            } else {
                exchange.sendResponseHeaders(statusCode, 0);
            }
        } catch (NotFoundException e) { // задача не найдена
            exchange.sendResponseHeaders(404, 0);
        } catch (TaskIntersectionException e) { // задача пересекается по времени исполнения
            exchange.sendResponseHeaders(406, 0);
            exchange.getResponseBody().write(e.getMessage().getBytes());
        } catch (Exception e) { // ошибка сервера
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write(e.getMessage().getBytes());
        }
        exchange.close();
    }

    /**
     * GET метод для получения данных
     */
    public abstract String get(String[] path, String message);

    /**
     * POST метод для добавления/редактирования данных
     */
    public abstract String post(String[] path, String message);

    /**
     * DELETE метод для удаления данных
     */
    public abstract String delete(String[] path, String message);

    /**
     * Метод для получения объекта из строки
     */
    public <T extends Task> T fromJson(String json, Class<T> cl) {
        T obj = gson.fromJson(json, cl);

        String name = obj.getName();
        if (name == null || name.isEmpty()) {
            throw new TaskFromStringException("Некорректное наименование задачи");
        }

        TaskStates type = obj.getState();
        if (type == null) {
            if (obj.getId() != 0) {
                throw new TaskFromStringException("Некорректный статус задачи");
            }
            obj.setState(TaskStates.NEW);
        }

        if (obj.getStartTime().isPresent()) {
            if (obj.getDuration().isEmpty()) {
                throw new TaskFromStringException("Некорректное время окончания задачи");
            }
        }
        return obj;
    }

    /**
     * Метод для преобразования объекта в строку
     */
    public String toJson(Object object) {
        return gson.toJson(object);
    }
}
