package com.revpasswordmanager.util;

import java.time.LocalDateTime;

public class DateTimeUtil {

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime addMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }
}