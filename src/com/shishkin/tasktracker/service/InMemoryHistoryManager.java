package com.shishkin.tasktracker.service;

import com.shishkin.tasktracker.model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        // удаляем старую историю
        remove(task.getId());
        // добавляем связь
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        // удаляем ноду
        removeNode(history.get(id));
        // удаляем связь
        history.remove(id);
    }

    @Override
    public void remove(Collection<? extends Task> elements) {
        for (Task el : elements) {
            remove(el.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new LinkedList<>();

        Node node = head;
        while (node != null) {
            historyList.add(node.task);
            node = node.next;
        }

        return historyList;
    }

    private void linkLast(Task task) {
        Node node = new Node(task, tail,null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;

        // запоминаем ноду в ссылочном массиве для удаления
        history.put(task.getId(), node);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
    }

    private static class Node {
        private Task task;
        private Node prev;
        private Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(task, node.task) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task, next, prev);
        }
    }
}
