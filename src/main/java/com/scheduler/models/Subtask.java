package com.scheduler.models;

public class Subtask {
    private String description;
    private boolean completed;

    // Constructor, getters and setters
    public Subtask(String description) {
        this.description = description;
        this.completed = false;
    }

    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}