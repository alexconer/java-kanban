package com.shishkin.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId; // идентификатор эпика

    public Subtask(int epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String name, String description, TaskStates state) {
        super(id, name, description, state);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String name, String description, TaskStates state, LocalDateTime startTime, Duration duration) {
        super(id, name, description, state, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicId=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", state=" + getState() +
                '}';
    }
}
