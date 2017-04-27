package com.turlygazhy.tool;

import java.util.Date;

/**
 * Created by Yerassyl_Turlygazhy on 06-Mar-17.
 */
public class DateUtil {
    public static Date getThisMonday() {
        Date date = new Date();
        while (!date.toString().contains("Mon")) {
            date.setDate(date.getDate() - 1);
        }
        return date;
    }

    public static Date getThisSunday() {
        Date date = new Date();
        while (!date.toString().contains("Sun")) {
            date.setDate(date.getDate() + 1);
        }
        return date;
    }

    public static Date getNextMonth() {
        Date date = new Date();
        date.setDate(date.getDate() + 1);
        while (date.getDate() != 1) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(1);
        return date;
    }

    public static Date getNextWeek() {
        Date date = new Date();
        date.setDate(date.getDate() + 1);
        while (!date.toString().contains("Mon")) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(1);
        return date;
    }

    public static Date getNextNight() {
        Date date = new Date();
        date.setDate(date.getDate() + 1);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(1);
        return date;
    }

    public static boolean checkHour(int hour) {
        Date date = new Date();
        return date.getHours() == hour;
    }

    public static Date getHour(int hour) {
        Date date = new Date();
        if (date.getHours() >= hour) {
            date.setDate(date.getDate() + 1);
        }
        date.setHours(hour);
        date.setMinutes(0);
        date.setSeconds(1);
        return date;
    }

    public static boolean isNewWeek() {
        Date date = new Date();
        return date.toString().contains("Mon");
    }
}
