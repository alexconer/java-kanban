package com.shishkin.tasktracker;

import com.shishkin.tasktracker.enums.TaskStates;

public class Task {
    private int id; // идентификатор задачи
    private String name; // наименование задачи
    private String description; // описание задачи
    private TaskStates state; // статус задачи

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.state = TaskStates.NEW;
    }

    public Task(int id, String name, String description, TaskStates state) {
        this.name = name;
        this.description = description;
        this.state = state;
        setId(id);
    }

    // getters and setters для идентификатора
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // getters для наименования и описания задачи (setters не требуются, т.к. параметрны задаются через конструктор)
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStates getState() {
        return state;
    }

    // setters для статуса задачи (доступен только наследникам класса, изменить статус можно только перевозданием класса)
    protected void setState(TaskStates state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
