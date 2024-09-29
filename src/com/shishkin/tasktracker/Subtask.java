package com.shishkin.tasktracker;

import com.shishkin.tasktracker.enums.TaskStates;

public class Subtask extends Task{
    private int epicId; // идентификатор эпика

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(int id, String name, String description, TaskStates state) {
        super(id, name, description, state);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
