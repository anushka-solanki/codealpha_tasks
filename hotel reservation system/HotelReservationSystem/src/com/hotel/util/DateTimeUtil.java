package com.hotel.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss");

    public static String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DISPLAY_FORMATTER);
    }

    public static long calculateDaysBetween(String checkInStr, String checkOutStr) {
        try {
            LocalDate in = LocalDate.parse(checkInStr, DATE_FORMATTER);
            LocalDate out = LocalDate.parse(checkOutStr, DATE_FORMATTER);
            return ChronoUnit.DAYS.between(in, out);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isDateOverlap(String start1Str, String end1Str, String start2Str, String end2Str) {
        try {
            LocalDate s1 = LocalDate.parse(start1Str, DATE_FORMATTER);
            LocalDate e1 = LocalDate.parse(end1Str, DATE_FORMATTER);
            LocalDate s2 = LocalDate.parse(start2Str, DATE_FORMATTER);
            LocalDate e2 = LocalDate.parse(end2Str, DATE_FORMATTER);

            // True if: start1 < end2 AND start2 < end1
            return s1.isBefore(e2) && s2.isBefore(e1);
        } catch (Exception e) {
            return false;
        }
    }
}
