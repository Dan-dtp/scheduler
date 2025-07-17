package com.scheduler.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.scheduler.models.*;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StorageManager {
    private static final String TASKS_FILE = "tasks.json";
    private static final String CATEGORIES_FILE = "categories.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .create();

    public static void saveData(TaskManager taskManager) throws IOException {
        // Save tasks
        try (Writer writer = new FileWriter(TASKS_FILE)) {
            gson.toJson(taskManager.getTasks(), writer);
        }

        // Save categories
        try (Writer writer = new FileWriter(CATEGORIES_FILE)) {
            gson.toJson(taskManager.getCategories(), writer);
        }
    }

    public static void loadData(TaskManager taskManager) throws IOException {
        // Load tasks
        File tasksFile = new File(TASKS_FILE);
        if (tasksFile.exists()) {
            try (Reader reader = new FileReader(tasksFile)) {
                Type taskListType = new TypeToken<List<Task>>(){}.getType();
                List<Task> tasks = gson.fromJson(reader, taskListType);
                if (tasks != null) {
                    tasks.forEach(taskManager::addTask);
                }
            }
        }

        // Load categories
        File categoriesFile = new File(CATEGORIES_FILE);
        if (categoriesFile.exists()) {
            try (Reader reader = new FileReader(categoriesFile)) {
                Type categoryListType = new TypeToken<List<Category>>(){}.getType();
                List<Category> categories = gson.fromJson(reader, categoryListType);
                if (categories != null) {
                    taskManager.getCategories().clear();
                    categories.forEach(taskManager.getCategories()::add);
                }
            }
        }
    }

    // Adapter for LocalDateTime
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    // Adapter for LocalDate
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    // Adapter for Color
    private static class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("r", src.getRed());
            obj.addProperty("g", src.getGreen());
            obj.addProperty("b", src.getBlue());
            return obj;
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            return new Color(
                    obj.get("r").getAsInt(),
                    obj.get("g").getAsInt(),
                    obj.get("b").getAsInt()
            );
        }
    }
}
