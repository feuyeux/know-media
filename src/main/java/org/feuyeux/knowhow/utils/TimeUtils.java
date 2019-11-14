package org.feuyeux.knowhow.utils;

import java.util.Calendar;

import lombok.extern.slf4j.Slf4j;

/**
 * @author feuyeux@gmail.com
 * @date 2019/09/01
 */
@Slf4j
public class TimeUtils {
    static final org.joda.time.format.DateTimeFormatter dtf = org.joda.time.format.ISODateTimeFormat.dateTime();
    static final org.joda.time.format.DateTimeFormatter dtxf = org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis();
    static final java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    static final java.time.format.DateTimeFormatter f3 = java.time.format.DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm::SS");
    static final java.time.format.DateTimeFormatter f2 = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SSS'Z'");
    static final java.text.SimpleDateFormat f0 = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", java.util.Locale.ENGLISH);

    public static String trans(String originalTime) {
        String s = trans0(originalTime);
        if (s != null) {
            return s;
        }
        s = trans2(originalTime);
        if (s != null) {
            return s;
        }
        s = trans1(originalTime);
        if (s != null) {
            return s;
        }
        s = trans3(originalTime);
        if (s != null) {
            return s;
        }
        return trans4(originalTime);
    }

    static String trans0(String originalTime) {
        Calendar c = Calendar.getInstance();
        try {
            java.util.Date date = f0.parse(originalTime);
            c.setTime(date);
        } catch (Exception e) {
            return null;
        }
        int monthValue = c.get(Calendar.MONTH) + 1;
        return toYearMonth(monthValue, c.get(Calendar.YEAR));
    }

    static String trans1(String originalTime) {
        org.joda.time.DateTime ct;
        try {
            ct = dtf.parseDateTime(originalTime);
        } catch (Exception e) {
            return null;
        }
        return toYearMonth(ct.getMonthOfYear(), ct.getYear());
    }

    static String trans2(String originalTime) {
        org.joda.time.DateTime ct;
        try {
            ct = dtxf.parseDateTime(originalTime);
        } catch (Exception e) {
            return null;
        }
        return toYearMonth(ct.getMonthOfYear(), ct.getYear());
    }

    static String trans3(String originalTime) {
        java.time.LocalDate date;
        try {
            date = java.time.LocalDate.parse(originalTime, f);
        } catch (Exception e) {
            return null;
        }
        return toYearMonth(date.getMonthValue(), date.getYear());
    }

    static String trans4(String originalTime) {
        java.time.LocalDate date;
        try {
            date = java.time.LocalDate.parse(originalTime, f2);
        } catch (Exception e) {
            try {
                date = java.time.LocalDate.parse(originalTime, f3);
            } catch (Exception e1) {
                log.info("Ignore to trans:" + originalTime);
                return "1900-01";
            }
        }
        return toYearMonth(date.getMonthValue(), date.getYear());
    }

    private static String toYearMonth(int monthValue, int year) {
        return year + "-" + (monthValue < 10 ? "0" + monthValue : monthValue);
    }
}
