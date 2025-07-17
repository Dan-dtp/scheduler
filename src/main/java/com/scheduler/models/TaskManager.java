package com.scheduler.models;

import java.awt.*;
import java.util.*;
import java.time.LocalDate;
import java.util.List;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public TaskManager() {
        // Initialize with default categories
        categories.add(new Category("Work", Color.BLUE));
        categories.add(new Category("Personal", Color.GREEN));
        categories.add(new Category("Family", Color.ORANGE));
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDueDate().toLocalDate().equals(date)) {
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
        if (categories.isEmpty()) {
            // Add default categories if empty
            categories.add(new Category("Work", Color.BLUE));
            categories.add(new Category("Personal", Color.GREEN));
        }
        return Collections.unmodifiableList(categories);
    }
}