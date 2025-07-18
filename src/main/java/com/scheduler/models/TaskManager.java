package com.scheduler.models;

import java.util.*;
import java.time.LocalDate;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<Category> categories = new ArrayList<>(); // Changed to modifiable list
    private List<Task> completedTasks = new ArrayList<>();

    public synchronized void completeTask(Task task) {
        if (tasks.remove(task)) {
            task.setCompleted(true);
            completedTasks.add(task);
            Collections.sort(completedTasks);
        }
    }

    public List<Task> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }

    public synchronized void uncompleteTask(Task task) {
        if (completedTasks.remove(task)) {
            task.setCompleted(false);
            tasks.add(task);
            Collections.sort(tasks);
        }
    }

    public synchronized List<Task> getTasks() {
        return new ArrayList<>(tasks); // Return defensive copy
    }

    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getDueDate().toLocalDate().equals(date)) {
                result.add(task);
            }
        }
        return result;
    }

    public synchronized void addTask(Task task) {
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        tasks.add(task);
        Collections.sort(tasks);
    }


    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    public List<Category> getCategories() {
        return categories; // Now returns modifiable list directly
    }
}