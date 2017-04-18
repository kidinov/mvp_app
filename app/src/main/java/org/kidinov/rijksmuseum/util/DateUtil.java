package org.kidinov.rijksmuseum.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Set of handy date related methods
 */
public class DateUtil {

    /**
     * @param daysDiff number of date which need to be added to current date. Can be positive or negative
     * @param format   format of output date, e.g. {@link C#AGENDA_API_DATE_FORMAT}
     * @return Formatted date
     */
    public static String getDateNDaysDiffAndFormat(int daysDiff, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(getDateNDaysDiff(daysDiff));
    }

    /**
     * @param daysDiff number of date which need to be added to current date. Can be positive or negative
     * @return Date
     */
    public static Date getDateNDaysDiff(int daysDiff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysDiff);
        return calendar.getTime();
    }

    /**
     * Returns difference between {@code @date1} and {@code @date2} in given {@code @timeUnit}
     *
     * @return difference in given {@code @timeUnit}
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    /**
     * Parse string represents date
     *
     * @param date   string represents date
     * @param format format of string, e.g. {@link C#AGENDA_API_DATE_FORMAT}
     * @return parsed Date
     */
    public static Date parseDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Timber.e(e, "can't parse date %s with format %s", date, format);
            throw new RuntimeException(e);
        }
    }
}
