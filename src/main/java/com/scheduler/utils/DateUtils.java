package com.scheduler.utils;

import java.time.*;
import java.time.format.*;
import java.time.temporal.TemporalAdjusters;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalDateTime parseDateTime(String dateStr, String timeStr) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMAT);
        LocalTime time = LocalTime.parse(timeStr, TIME_FORMAT);
        return LocalDateTime.of(date, time);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMAT);
    }

    public static LocalDate nextWeekday(LocalDate date, DayOfWeek dayOfWeek) {
        return date.with(TemporalAdjusters.next(dayOfWeek));
    }
}