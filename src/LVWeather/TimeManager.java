package LVWeather;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class TimeManager {
    // This class exists to reduce the amount of pain when dealing with the
    // date/time Strings.
    static String DATE = LocalDate.now().toString().replace("-", "");
    static String TIME = LocalTime.now().toString();
    static String DATE_TIME = DATE + TIME.split(":")[0] + "00";
    static DateTimeFormatter formatterOld = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // TIME_HOUR_LIST not needed anymore
    /*
     * static String[] TIME_HOUR_LIST = {"00:00", "01:00", "02:00", "03:00",
     * "04:00", "05:00",
     * "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
     * "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
     * "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",};
     */

    // RAW date time is 'yyyyMMDDHHmm'
    // Formatted date time is 'yyyy.MM.DD HH:mm'

    static String formatRawDateTime(String[] arr) {
        return formatRawDate(arr[0]) + " " + formatRawTime(arr[1]);
    }

    static String[] formatRawDateTimeToFormattedArray(String[] arr) {
        String[] out = {formatRawDate(arr[0]), formatRawTime(arr[1])};
        return out;
    }

    static String formatRawDate(String date) {
        return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8);
    }

    static String formatRawTime(String time) {
        return time.substring(0, 2) + ":" + time.substring(2, 4);
    }

    static String formatRawDateTime(String str) {
        return str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8) + " " + str.substring(8, 10) + ":" + str.substring(10, 12);
    }

    static String formatToRawDateTime(String date, String time) {
        return date.replace(".", "") + time.replace(":", "");
    }

    static String formatToRawDateTime(String dateTime) {
        if (dateTime.contains("T")) {
            dateTime.replace("T", " ");
        }
        return dateTime.replace(".", "").replace(":", "").replace("-", "").replace(" ", "").substring(0, 12);
    }

    static String formatToDBDateTime(String date, String time) {
        return date.replace(".", "-") + " " + time + ":00";
    }

    static String formatToDBDateTime(String dateTime) {
        return dateTime.substring(0, 4) + "." + dateTime.substring(4, 6) + "." + dateTime.substring(6, 8) + " " + dateTime.substring(8, 10) + ":" + dateTime.substring(10, 12);
    }

    static String formatToRawDate(String date) {
        return date.replace(".", "");
    }

    static String formatToRawTime(String date) {
        return date.replace(":", "");
    }

    static String formatDateFromDashesToDots(String date) {
        return date.replace("-", ".");
    }

    static String formatDateFromDotsToDashes(String date) {
        return date.replace(".", "-");
    }

    static String getCurDate() {
        return DATE;
    }

    static String getCurHour() {
        return TIME.substring(0, 2);
    }

    static String getCurDateTime() {
        return DATE + TIME.substring(0, 2) + "00";
    }

    static String getNextHour() {
        int hour = Integer.parseInt(TIME.split(":")[0] + "00");
        hour += 100;
        hour %= 2400;
        return Integer.toString(hour);
    }

    static String getTodaysDateWithDashes() {
        return LocalDate.now().toString();
    }

    // this is 100% stolen
    static String addOneDayCalendarToDBDate(String date)  {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
            c.add(Calendar.DATE, 1);
        } catch (Exception e) {
            System.out.println("TimeManager - addOneDayCalendarToDBDate(" +date+ "): " +e);
        }
        return sdf.format(c.getTime());
    }

    static String getRawDateFromRawDateTime(String dateTime) {
        return dateTime.substring(0, 8);
    }

    static String getRawTimeFromRawDateTime(String dateTime) {
        return dateTime.substring(8, 12);
    }

    static String getFormattedTimeFromRawDateTime(String dateTime) {
        return dateTime.substring(8, 10) +":"+ dateTime.substring(10, 12);
    }
}
