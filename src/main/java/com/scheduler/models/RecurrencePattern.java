package com.scheduler.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;

public class RecurrencePattern {
    public enum Frequency { DAILY, WEEKLY, MONTHLY, YEARLY }

    private Frequency frequency;
    private int interval;
    private EnumSet<DayOfWeek> daysOfWeek;
    private int dayOfMonth;
    private LocalDate endDate;

    // Getters and setters
    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public void setInterval(int interval) { this.interval = interval; }
    public void setDaysOfWeek(EnumSet<DayOfWeek> days) { this.daysOfWeek = days; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean occursOn(LocalDate date) {
        // Implementation of recurrence logic
        return false;
    }
}