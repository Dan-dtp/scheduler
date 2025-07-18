package com.scheduler.models;

import java.awt.Color;
import java.util.Random;

public class Category {
    private String name;
    private Color color;
    private static final Color[] PALETTE = {
            new Color(100, 180, 255), // Blue
            new Color(255, 150, 100), // Coral
            new Color(180, 220, 130), // Green
            new Color(255, 200, 100), // Yellow
            new Color(200, 160, 255)  // Purple
    };

    public Category(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Category(String name) {
        this(name, PALETTE[new Random().nextInt(PALETTE.length)]);
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    @Override
    public String toString() {
        return name;
    }
}