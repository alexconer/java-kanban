package com.shishkin.tasktracker.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task{
    private final ArrayList<Integer> subtasks =  new ArrayList<>(); // список подзадач

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(name, description);
        setId(id);
    }

    // добавление подзадач
    public void addSubtask(int id){
        subtasks.add(id);
    }

    // удаление подзадачи
    public void removeSubtask(int id){
        subtasks.remove(Integer.valueOf(id));
    }

    // удвление всех подзадач
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    // получение списка идентификаторов подзадач
    public ArrayList<Integer> getSubtasksIds() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", state=" + getState() +
                ", subtasks=[" + Arrays.toString(subtasks.toArray()) + ']' +
                '}';
    }

}
