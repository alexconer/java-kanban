package com.shishkin.tasktracker.model;

public class Subtask extends Task{
    private final int epicId; // идентификатор эпика

    public Subtask(int epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String name, String description, TaskStates state) {
        super(id, name, description, state);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
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
