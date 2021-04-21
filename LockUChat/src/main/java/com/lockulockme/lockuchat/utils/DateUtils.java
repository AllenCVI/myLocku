package com.lockulockme.lockuchat.utils;

import com.lockulockme.lockuchat.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(long milliseconds, boolean abbreviate) {
        String dataString;
        String timeStringBy24;

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = ResouseUtils.getResouseString(R.string.today);
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = ResouseUtils.getResouseString(R.string.yesterday);
        }
        else if (is1Week(currentTime, today)) {
            dataString = getWeek4Date(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (abbreviate) {
            if (!currentTime.before(todaybegin)) {
                return getTodayTimeFormat(currentTime);
            } else {
                return dataString;
            }
        } else {
            return dataString + " " + timeStringBy24;
        }
    }
    public static String getTodayTimeFormat(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }
    public static String getWeek4Date(Date date) {
        String[] weeknames = ResouseUtils.getResouseStringArray(R.array.weekname);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeknames[intWeek];
    }
    public static boolean is1Week(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }
}
