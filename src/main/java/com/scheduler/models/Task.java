package com.scheduler.models;

import java.time.*;
import java.util.*;

public class Task implements Comparable<Task> {
    private UUID id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime dueDate;
    private Category category;
    private boolean completed;
    private Set<String> tags;
    private List<Subtask> subtasks;
    private RecurrencePattern recurrence;

    public Task() {
        this.id = UUID.randomUUID();
        this.tags = new HashSet<>();
        this.subtasks = new ArrayList<>();
        this.priority = Priority.MEDIUM;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }
    public List<Subtask> getSubtasks() { return Collections.unmodifiableList(subtasks); }
    public RecurrencePattern getRecurrence() { return recurrence; }
    public void setRecurrence(RecurrencePattern recurrence) { this.recurrence = recurrence; }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void addTag(String tag) {
        tags.add(tag.toLowerCase());
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag.toLowerCase());
    }

    @Override
    public int compareTo(Task other) {
        // Handle null due dates
        if (this.dueDate == null && other.dueDate == null) return 0;
        if (this.dueDate == null) return -1;
        if (other.dueDate == null) return 1;
        return this.dueDate.compareTo(other.dueDate);
    }
}