package LVWeather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class ForecastDataStorage extends WeatherDataStorage {

    ForecastDataStorage(DB db, String townName, String date, String time) {
        super(db, townName, date, time);
    }

    public boolean updateData() {
        if (placeFullName == null || placeFullName.isBlank() || dateTime == null || dateTime.isBlank()) {
            return false;
        }
        if (townName == null || townName.isBlank() || regionName == null || regionName.isBlank() || placeID == null) {
            if (!setAllLocationInfo(placeFullName))
                return false;
        }

        if (this.cachedData != null && cachedData.get(placeID) != null && cachedData.get(placeID).size() > 0) {
            System.out.println("Data already cached for: " + placeFullName + " (point = " + placeID + ") with size of " +cachedData.get(placeID).size());
            return true;
        }

        System.out.println("Receiving data for town " + placeFullName + " (point = " + placeID + ")...");

        int requestID = -1;
        try {
            requestID = Integer.parseInt(placeID);
        } catch (Exception e) {
            System.out.println("ERROR parsing requestID int to String for " + placeFullName + " (point = " + placeID + ") because receivedData == null");
        }
        HashMap<String, String[]> receivedData = ForecastPointScraper.getFullForecastTableArrayForPlace(requestID);

        if (receivedData == null) {
            System.out.println("...failed to receive data for town " + placeFullName + " (point = " + placeID + ") because receivedData == null");
            return false;
        }
        System.out.println("...successfully received data for town " + placeFullName + " (point = " + placeID + ")");
        if (this.cachedData == null) {
            cachedData = new HashMap<String, HashMap<String, String[]>>();
            System.out.println("No cachedData found...\n...creating a new hashmap with " + placeFullName + " (point = " + placeID + ")...");
        }

        this.cachedData.put(placeID, receivedData);
        System.out.println("Caching " + placeFullName + " (point = " + placeID + ") with size of " +cachedData.size()+ "...");

        //printCache();

        if (validTimesPerDate == null) {
            updateValidDateTimes();
        }

        return true;
    }


    public boolean setAllLocationInfo(String placeFullName) {
        if (placeFullName == null || placeFullName.isBlank()) {
            return false;
        }
        String[] splitTownFullName = splitTownFullName(placeFullName);

        // fetchTownID returns -1 if such town doesn't exist
        // All pointID's are positive integers, therefore if -2 isn't changed to a new positive pointID we dont need to update the location info
        int receivedTownID = -2;
        if (this.placeFullName != placeFullName) {
            receivedTownID = fetchTownID(this.db, splitTownFullName);
        }
        if (receivedTownID == -1) {
            return false;
        }

        if (receivedTownID <= 0) {
            return true;
        }
        this.placeID = Integer.toString(receivedTownID);
        this.placeFullName = placeFullName;
        this.townName = splitTownFullName[0];
        this.regionName = splitTownFullName[1];

        return true;
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


    public int fetchTownID() {
        if (this.townName != null && this.regionName != null) {
            return db.getPointIDFromName(this.townName, this.regionName);
        }
        return -1;
    }

    public static int fetchTownID(DB db, String[] splitTownFullName) {
        return db.getPointIDFromName(splitTownFullName[0], splitTownFullName[1]);
    }
}
