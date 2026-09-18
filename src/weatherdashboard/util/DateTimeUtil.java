package weatherdashboard.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility functions for formatting Unix timestamps and Java Date objects into human-readable strings.
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        // Restrict instantiation
    }

    public static String formatHeaderDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a");
        return sdf.format(date);
    }

    public static String formatTime12Hour(long unixTimestampSeconds) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(new Date(unixTimestampSeconds * 1000));
    }

    public static String formatDayAbbreviation(long unixTimestampSeconds) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        return dayFormat.format(new Date(unixTimestampSeconds * 1000));
    }
}
