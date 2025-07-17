package com.scheduler.models;

import java.awt.Color;

public class Category {
    private String name;
    private Color color;

    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    // Getters and setters
    public String getName() { return name; }
    public Color getColor() { return color; }

    @Override
    public String toString() {
        return name;
    }
}