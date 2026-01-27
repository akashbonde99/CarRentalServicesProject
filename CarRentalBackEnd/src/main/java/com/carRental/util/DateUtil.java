package com.carRental.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    public static long calculateDays(LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        return days <= 0 ? 1 : days;
    }
}
