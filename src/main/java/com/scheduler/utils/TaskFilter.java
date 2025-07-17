package com.scheduler.utils;

import com.scheduler.models.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class TaskFilter {
    private String searchText;
    private Priority minPriority;
    private Set<Category> categories;
    private LocalDate startDate;
    private LocalDate endDate;

    public Predicate<Task> toPredicate() {
        return task -> {
            boolean matches = true;

            if (searchText != null && !searchText.isEmpty()) {
                matches &= task.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(searchText.toLowerCase());
            }

            if (minPriority != null) {
                matches &= task.getPriority().compareTo(minPriority) <= 0;
            }

            if (categories != null && !categories.isEmpty()) {
                matches &= categories.contains(task.getCategory());
            }

            if (startDate != null) {
                matches &= !task.getDueDate().toLocalDate().isBefore(startDate);
            }

            if (endDate != null) {
                matches &= !task.getDueDate().toLocalDate().isAfter(endDate);
            }

            return matches;
        };
    }

    // Builder pattern methods
    public TaskFilter withSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    public TaskFilter withMinPriority(Priority minPriority) {
        this.minPriority = minPriority;
        return this;
    }

    // ... other builder methods ...
}