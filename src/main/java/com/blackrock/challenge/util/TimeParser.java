package com.blackrock.challenge.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class TimeParser {
    private static final DateTimeFormatter FMT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss", Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final ZoneId ZONE = ZoneOffset.UTC;

    private TimeParser() {
    }

    public static long toEpochSecond(String ts) {
        LocalDateTime ldt = LocalDateTime.parse(ts, FMT);
        return ldt.atZone(ZONE).toEpochSecond();
    }
}