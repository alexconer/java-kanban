package com.shishkin.tasktracker.exception;

public class TaskFromStringException extends RuntimeException {
    public TaskFromStringException(String message) {
        super(message);
    }
}
