package com.scheduler.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateParser {
    public static LocalDateTime parse(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return LocalDateTime.parse(input, formatter);
    }
}
