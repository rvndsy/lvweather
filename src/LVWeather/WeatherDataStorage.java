package LVWeather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class WeatherDataStorage {

    protected String placeFullName = null; // stored as "Name, Parish/Region"
    protected String dateTime = null;      // stored as "YYYYMMDDHHmm" - this is 'RAW' dateTime format
    protected String townName = null;      // stored as "Name"
    protected String regionName = null;    // stored as "Parish/Region"
    protected String placeID = null;            // -1 means ID not yet set and it must be fetched from DB

    // First HashMap's String key is placeID which links to
    // nested Second HashMap where String key is dateTime and String[] are forecast values (table col2)
    protected HashMap<String, HashMap<String, String[]>> cachedData = null;
    // First String key is date and Second TreeSet stores time values in RAW format
    protected TreeMap<String, TreeSet<String>> validTimesPerDate = null;

    protected DB db = null;

    WeatherDataStorage(DB db, String townName, String date, String time) {
        this.db = db;
        System.out.println("\n\n" + time + "\n\n");
        update(townName, date, time);
        if (validTimesPerDate == null && cachedData != null) {
            updateValidDateTimes();
        }
    }

    protected abstract boolean updateData();
    protected abstract boolean setAllLocationInfo(String placeFullName);

    public boolean update(String placeFullName, String date, String time) {
        if (placeFullName == null || placeFullName.isBlank()) {
            return false;
        }
        if (date == null || date.isBlank() || time == null || time.isBlank()) {
            return false;
        }

        if (!setAllLocationInfo(placeFullName)) {
            return false;
        }

        if (date.contains(".") && time.contains(":")) {
            this.dateTime = formatToRawDateTime(date, time);
        } else {
            this.dateTime = date+time;
        }

        return updateData();
    }

    public String getTownFullName() {
        return placeFullName;
    }

    public String getTownID() {
        return placeID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String[] getCurData() {
        return cachedData.get(placeID).get(dateTime);
    }

    public boolean setDateTime(String date, String time) {
        if (date == null || date.isBlank() || time == null || time.isBlank()) {
            return false;
        }
        return setDateTime(formatToRawDateTime(date, time));
    }

    public boolean setDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return false;
        }
        this.dateTime = dateTime;
        return true;
    }

    /**
     * This method splits a full town name into its name and region separately as a
     * two element String[]
     *
     * @return town name at index 0 and region name at index 1
     */
    public String[] splitTownFullName(String placeFullName) {
        String[] location = { "", "" };
        if (placeFullName.contains(", ") && placeFullName.split(", ").length == 2) {
            location[0] = placeFullName.split(", ")[0];
            location[1] = placeFullName.split(", ")[1];
        } else {
            return null;
        }
        return location;
    }

    public String[] getAvailableTimesStringArray(String date) {
        System.out.println(placeID + " " + townName + " " + dateTime + " " + date);
        if (date.contains(".")) {
            date = TimeManager.formatToRawDate(date);
        }

        TreeSet<String> timeTreeSet = this.validTimesPerDate.get(date);
        if (timeTreeSet == null) {
            System.err.println("[WARN] Data for date " +date+ " is missing. Are DB tables up-to-date?");
            System.err.println("       Setting date to validTimesPerDate.firstKey()" +validTimesPerDate.firstKey());

            timeTreeSet = this.validTimesPerDate.get(validTimesPerDate.lastKey());
        }

        List<String> times = timeTreeSet.stream()
            .filter(s -> s.length() > 3)
            .map(s -> s.substring(0, 2) + ":" +s.substring(2, 4))
            .collect(Collectors.toList());

        return times.toArray(new String[times.size()]);
    }

    public String[] getAvailableDatesStringArray() {
        Set<String> keySet = this.validTimesPerDate.keySet();

        List<String> times = new ArrayList<>(keySet).stream()
            .filter(s -> s.length() == 8)
            .map(s -> TimeManager.formatRawDate(s))
            .collect(Collectors.toList());

        return times.toArray(new String[times.size()]);
    }

    /**
     * Should run only on the first cache update
     */
    protected boolean updateValidDateTimes() {
        System.out.println("Updating valid forecast date times...");
        if (cachedData == null) {
            System.out.println("...failed to update valid forecast date times because cachedData == null");
            return false;
        }

        String date;
        String time;

        TreeMap<String, TreeSet<String>> validTimes = new TreeMap<String, TreeSet<String>>();
        for (String key : cachedData.get(placeID).keySet()) {
            date = TimeManager.getRawDateFromRawDateTime(key);
            time = TimeManager.getRawTimeFromRawDateTime(key);
            if (validTimes.get(date) == null) {
                validTimes.put(date, new TreeSet<String>());
                validTimes.get(date).add(time);
            } else {
                validTimes.get(date).add(time);
            }
        }
        this.validTimesPerDate = validTimes;
        System.out.println("...successfully updated valid forecast date times");
        return true;
    }

    public String[] getNextValidDateTime(String date, String time) {
        //System.out.println("Finding the next date & time for " +date+ " " +time+ "...");

        String dateTime[] = new String[2];
        dateTime[0] = date;
        dateTime[1] = time;
        if (date == null || date.isBlank()) {
            dateTime[0] = TimeManager.getCurDate();
        }
        if (time == null || time.isBlank()) {
            dateTime[1] = this.validTimesPerDate.get(date).first();
        }

        if (time.equals(this.validTimesPerDate.get(date).last())) {
            if (this.validTimesPerDate.higherKey(date) != null) {
                dateTime[0] = this.validTimesPerDate.higherKey(date);
                dateTime[1] = this.validTimesPerDate.get(dateTime[0]).first();
            }
            return dateTime;
        } else {
            dateTime[0] = date;
        }

        TreeSet<String> treeSet = this.validTimesPerDate.get(date);
        for (String str : treeSet) {
            if (str.equals(treeSet.last())) {
                break;
            }
            if (str.equals(time)) {
                dateTime[1] = treeSet.higher(str);
                break;
            }
        }

        return dateTime;
    }

    public String[] getPrevValidDateTime(String date, String time) {
        //System.out.println("Finding the previous date & time for " +date+ " " +time+ "...");

        String dateTime[] = new String[2];
        dateTime[0] = date;
        dateTime[1] = time;
        if (date == null || date.isBlank()) {
            dateTime[0] = TimeManager.getCurDate();
        }
        if (time == null || time.isBlank()) {
            dateTime[1] = this.validTimesPerDate.get(date).first();
        }

        if (time.equals(this.validTimesPerDate.get(date).first())) {
            if (this.validTimesPerDate.lowerKey(date) != null) {
                dateTime[0] = this.validTimesPerDate.lowerKey(date);
                dateTime[1] = this.validTimesPerDate.get(dateTime[0]).last();
            }
            return dateTime;
        } else {
            dateTime[0] = date;
        }

        TreeSet<String> treeSet = this.validTimesPerDate.get(date);
        for (String str : treeSet) {
            if (str.equals(treeSet.first())) {
                continue;
            }
            if (str.equals(time)) {
                dateTime[1] = treeSet.lower(str);
                break;
            }
        }

        return dateTime;
    }

    public static String formatToRawDateTime(String date, String time) {
        return TimeManager.formatToRawDateTime(date, time);
    }

    public static String[] formatToRawDateTimePair(String date, String time) {
        String formatted[] = { TimeManager.formatToRawDate(date), TimeManager.formatToRawTime(time) };
        return formatted;
    }

    protected void printCache() {
        for (String key1 : cachedData.keySet()) {
            for (String key2 : cachedData.get(key1).keySet()) {
                System.out.print(key1 + " " +key2);
                for (String str : cachedData.get(key1).get(key2)) {
                        System.out.print(str+ "\t");
                }
                System.out.println();
            }
        }
    }
}
