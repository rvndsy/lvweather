package LVWeather;

import java.util.HashMap;

public class StationDataStorage extends WeatherDataStorage {

    StationDataStorage(DB db, String townName, String date, String time) {
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

        HashMap<String, String[]> receivedData = db.getStationDataForDateAndPlace(townName, dateTime);
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

        String receivedStationCode = fetchStationCode(placeFullName);
        if (receivedStationCode == null || receivedStationCode == "null" || receivedStationCode.isBlank()) {
            return false;
        }

        this.placeID = receivedStationCode;
        this.placeFullName = placeFullName;
        this.townName = placeFullName;
        this.regionName = placeFullName;

        return true;
    }

    public String formatToDateTime(String date, String time) {
        return TimeManager.formatToDBDateTime(date, time);
    }

    public String fetchStationCode(String townName) {
        return db.getStationCodeFromName(townName);
    }
}
