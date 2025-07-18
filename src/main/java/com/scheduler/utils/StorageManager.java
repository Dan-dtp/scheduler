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
    static final String TASKS_FILE = "tasks.json";
    private static final String CATEGORIES_FILE = "categories.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())  // Separate adapter for LocalDate
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .create();

    public static void saveData(TaskManager taskManager) throws IOException {
        JsonObject root = new JsonObject();
        root.add("tasks", gson.toJsonTree(taskManager.getTasks()));
        root.add("completedTasks", gson.toJsonTree(taskManager.getCompletedTasks()));

        try (Writer writer = new FileWriter(TASKS_FILE)) {
            gson.toJson(root, writer);
        }
    }

    public static void loadData(TaskManager taskManager) throws IOException {
        File file = new File(TASKS_FILE);
        if (!file.exists()) {
            initializeEmptyDataFile();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            if (root == null) {
                initializeEmptyDataFile();
                return;
            }

            Type taskListType = new TypeToken<List<Task>>(){}.getType();

            // Load regular tasks
            List<Task> tasks = gson.fromJson(root.get("tasks"), taskListType);
            if (tasks != null) {
                taskManager.getTasks().clear();
                taskManager.getTasks().addAll(tasks);
            }

            // Load completed tasks
            List<Task> completedTasks = gson.fromJson(root.get("completedTasks"), taskListType);
            if (completedTasks != null) {
                taskManager.getCompletedTasks().clear();
                taskManager.getCompletedTasks().addAll(completedTasks);
            }
        }
    }

    static void initializeEmptyDataFile() throws IOException {
        JsonObject root = new JsonObject();
        root.add("tasks", new JsonArray());
        root.add("completedTasks", new JsonArray());

        try (Writer writer = new FileWriter(TASKS_FILE)) {
            gson.toJson(root, writer);
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