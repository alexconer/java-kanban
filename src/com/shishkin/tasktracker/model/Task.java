package com.shishkin.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id; // идентификатор задачи
    private String name; // наименование задачи
    private String description; // описание задачи
    private TaskStates state; // статус задачи
    private Duration duration; // продолжительность задачи
    private LocalDateTime startTime; // дата и время начала задачи

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.state = TaskStates.NEW;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this(name, description);
        setStartTimeAndDuration(startTime, duration);
    }

    public Task(int id, String name, String description, TaskStates state) {
        this.name = name;
        this.description = description;
        this.state = state;
        setId(id);
    }

    public Task(int id, String name, String description, TaskStates state, LocalDateTime startTime, Duration duration) {
        this(id, name, description, state);
        setStartTimeAndDuration(startTime, duration);
    }

    // getters and setters для идентификатора
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // getters и setters для наименования и описания задачи
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStates getState() {
        return state;
    }

    // setters для статуса задачи
    public void setState(TaskStates state) {
        this.state = state;
    }

    // возвращает тип задачи
    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    // getters и setters для продолжительности и даты начала задач
    public void setStartTimeAndDuration(LocalDateTime startTime, Duration duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    // возвращает дату и время завершения задачи
    public LocalDateTime getEndTime() {
        if (startTime == null) return null;
        return startTime.plus(duration);
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
