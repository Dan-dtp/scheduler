package com.scheduler.utils;

import com.scheduler.models.*;
import java.io.*;
import java.time.*;
import java.time.format.*;

public class CommandLineInterface {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        try {
            // Ensure data file exists with proper structure
            File file = new File(StorageManager.TASKS_FILE);
            if (!file.exists()) {
                StorageManager.initializeEmptyDataFile();
            }

            // Load existing data
            TaskManager taskManager = new TaskManager();
            StorageManager.loadData(taskManager);

            // Create and add task
            Task task = createTaskFromArgs(args);
            taskManager.addTask(task);
            StorageManager.saveData(taskManager);

            System.out.println("Task added successfully!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static Task createTaskFromArgs(String[] args) {
        Task task = new Task();
        task.setTitle(args[0]);
        task.setDescription(args[1]);

        if (args.length > 2) {
            try {
                task.setDueDate(LocalDateTime.parse(args[2], DATE_FORMAT));
            } catch (DateTimeParseException e) {
                System.err.println("Warning: Invalid date format. Using current time.");
                task.setDueDate(LocalDateTime.now());
            }
        }

        if (args.length > 3) {
            try {
                task.setPriority(Priority.valueOf(args[3].toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Warning: Invalid priority. Using MEDIUM.");
                task.setPriority(Priority.MEDIUM);
            }
        }

        return task;
    }
}