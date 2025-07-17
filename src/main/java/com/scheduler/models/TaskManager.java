package com.scheduler.models;

import java.util.*;
import java.time.LocalDate;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<Category> categories = new ArrayList<>(); // Changed to modifiable list

    public List<Task> getTasks() {
        return tasks; // Now returns modifiable list directly
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

    public void addTask(Task task) {
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