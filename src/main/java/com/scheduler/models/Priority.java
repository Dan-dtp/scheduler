package com.scheduler.models;

import java.awt.Color;

public enum Priority {
    HIGH(1, "High", new Color(255, 100, 100)),
    MEDIUM(2, "Medium", new Color(255, 200, 100)),
    LOW(3, "Low", new Color(100, 200, 100));

    private final int value;
    private final String displayName;
    private final Color color;

    Priority(int value, String displayName, Color color) {
        this.value = value;
        this.displayName = displayName;
        this.color = color;
    }

    // Getters
    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }
    public Color getColor() { return color; }
}