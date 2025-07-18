package com.scheduler.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.scheduler.models.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class StorageManager {
    private static final String TASKS_FILE = "tasks.json";
    private static final String CATEGORIES_FILE = "categories.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())  // Separate adapter for LocalDate
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .create();

    public static void saveData(TaskManager taskManager) throws IOException {
        JsonObject data = new JsonObject();
        data.add("tasks", gson.toJsonTree(taskManager.getTasks()));
        data.add("completedTasks", gson.toJsonTree(taskManager.getCompletedTasks()));

        try (Writer writer = new FileWriter(TASKS_FILE)) {
            gson.toJson(data, writer);
        }
    }

    public static void loadData(TaskManager taskManager) throws IOException {
        // Load tasks
        File tasksFile = new File(TASKS_FILE);
        if (tasksFile.exists()) {
            try (Reader reader = new FileReader(tasksFile)) {
                Type taskListType = new TypeToken<List<Task>>(){}.getType();
                List<Task> tasks = gson.fromJson(reader, taskListType);
                System.out.println("Loaded tasks: " + tasks.size());
                if (tasks != null) {
                    System.out.println("[DEBUG] Loading " + tasks.size() + " tasks from file");
                    tasks.forEach(task -> {
                        System.out.println(" - " + task.getTitle());
                        taskManager.addTask(task);
                    });
                }
            }
        }

        File categoriesFile = new File(CATEGORIES_FILE);
        if (categoriesFile.exists()) {
            try (Reader reader = new FileReader(categoriesFile)) {
                Type categoryListType = new TypeToken<List<Category>>(){}.getType();
                List<Category> categories = gson.fromJson(reader, categoryListType);
                if (categories != null) {
                    List<Category> currentCategories = taskManager.getCategories();
                    if (currentCategories instanceof ArrayList) {
                        ((ArrayList<Category>) currentCategories).clear();
                    } else {
                        // Handle case where getCategories() returns unmodifiable list
                        throw new IllegalStateException("Categories list is not modifiable");
                    }
                    currentCategories.addAll(categories);
                }
            }
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString());
        }
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }

    private static class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public JsonElement serialize(Color color, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("r", color.getRed());
            obj.addProperty("g", color.getGreen());
            obj.addProperty("b", color.getBlue());
            return obj;
        }

        @Override
        public Color deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int r = obj.get("r").getAsInt();
            int g = obj.get("g").getAsInt();
            int b = obj.get("b").getAsInt();
            return new Color(r, g, b);
        }
    }
}