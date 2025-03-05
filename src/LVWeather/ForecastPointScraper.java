package LVWeather;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ForecastPointScraper {
    private static double LV_WEST = 20.9;
    private static double LV_EAST = 28.3;
    private static double LV_NORTH = 58.1;
    private static double LV_SOUTH = 55.6;
    private static String[] JSON_OBJ_KEYS = {"laiks", "nosaukums", "temperatura", "sajutu_temperatura", "veja_atrums", "brazmas", "veja_virziens"
            , "nokrisni_1h","nokrisnu_varbutiba", "sniegs", "perkons", "relativais_mitrums",
            "spiediens", "makoni", "uvi_indekss" };
    private static String[] TABLE_FIRST_COLUMN = {"Date and time:", "Place name: ", "Temperature: ", "'Feels like': ", "Wind speed: ", "Wind gusts: ", "Wind direction: "
            , "Precipitation (mm/1h): ","Precipitation probability: ", "Snow: ", "Thunderstorms: ", "Relative humidity: ",
            "Atmospheric pressure: ", "Cloud cover: ", "UVI Index: " };
    
        
    static String[][] getTableArrayFromJSONObject(JSONObject jsonObj) {
        String[][] temp2DStringArr = new String[JSON_OBJ_KEYS.length][];
        String dateTimeRawStr = jsonObj.get("laiks").toString();
        String dateTime = TimeManager.formatRawDateTime(dateTimeRawStr);
        String[] dateTimeRowPair = {TABLE_FIRST_COLUMN[0], dateTime};
        temp2DStringArr[0] = dateTimeRowPair;
        try {
            for (int i = 1; i < JSON_OBJ_KEYS.length; i++) {
                String[] tempStringRowPair = new String[2];
                tempStringRowPair[0] = (TABLE_FIRST_COLUMN[i]);
                String keyValueString = jsonObj.get(JSON_OBJ_KEYS[i]).toString();
                if (!keyValueString.equals("null")) {
                    String secondColumnString = keyValueString;
                    tempStringRowPair[1] = secondColumnString;    
                } else {
                    tempStringRowPair[1] = "no data";
                }
                temp2DStringArr[i] = tempStringRowPair;
            }
        } catch (Exception e) {
            System.out.println("Error in ForecastPoint get2DStringArrayFromJSONObject(): " +e);
        }
        return temp2DStringArr;
    }
    
    // gets all available dates for forecast
    static String[] getAvailableDatesStringArray() {
        JSONArray jsonArr = scrapeHourlyForecastFromPoint(11);    // which id does not matter here, as long as it exists
        ArrayList<String> dateArr = new ArrayList<String>();
        for (Object obj : jsonArr) {
            JSONObject jsonObj = (JSONObject) obj;
            String dateStr = jsonObj.getString("laiks");
            dateStr = dateStr.replace(dateStr.substring(8), "");
            if (!dateArr.contains(dateStr)) {                
                dateArr.add(dateStr);
            }
        }
        for (int i = 0; i < dateArr.size(); i++) {
            String date = dateArr.get(i).substring(0, 4) + "." + dateArr.get(i).substring(4, 6) +"."+ dateArr.get(i).substring(6, 8);
            dateArr.set(i, date);
        }
        return dateArr.toArray(new String[dateArr.size()]);
    }
    
    // gets all available times for forecast in a date
    static String[] getAvailableTimesStringArray(String date) {
        if (date.contains(".")) {
            date = TimeManager.formatToRawDate(date);
        }
        JSONArray jsonArr = scrapeHourlyForecastFromPoint(11);    // which id does not matter here, as long as it exists
        System.out.println("Date ---> " +date);
        ArrayList<String> timeArr = new ArrayList<String>();
        for (Object obj : jsonArr) {
            JSONObject jsonObj = (JSONObject) obj;
            String dateTimeStr = jsonObj.getString("laiks");
            String timeStr = dateTimeStr.replace(dateTimeStr.substring(0, 8), "");
            String dateStr = dateTimeStr.replace(dateTimeStr.substring(8), "");
            //System.out.println("IN getAvailableTimesStringArray DATE AND TIME OF " +dateStr+ "  " +timeStr+ " GIVEN DATE: " +date);
            if (dateStr.equals(date)) {                
                timeArr.add(timeStr);
            } else {
                continue;
            }
        }
        for (int i = 0; i < timeArr.size(); i++) {
            String time = timeArr.get(i).substring(0, 2) + ":" + timeArr.get(i).substring(2, 4);
            timeArr.set(i, time);
        }
        return timeArr.toArray(new String[timeArr.size()]);
    }
    
    // Scans a single rectangle / geographical area in Latvia confined by given coordinates
    // A single box rectangle is drawn starting from south-west (bottom-left) corner first, then one-by-one
    // each corner counter-clockwise and ends again in south-west corner (start corner == end corner).
    //
    // (probably not needed anymore, but here is an array of coords in the same order as in the url POLYGON).
    // static double[] LV_COORDS = {LV_WEST, LV_SOUTH, LV_EAST, LV_SOUTH, LV_EAST, LV_NORTH, LV_WEST, LV_NORTH, LV_WEST,
    // LV_SOUTH};
    // ^^^ LV_WEST, LV_SOUTH, etc. variables can be found at the top of this class. 
    // Each pair of west/east + south/north coordinates is a single corner LV_WEST
    // and LV_SOUTH make up the first corner, LV_EAST and LV_SOUTH is next one, and
    // etc.
    static JSONArray scrapeAllForecastPointsFromCoordsNow(double west, double east, double south, double north, int sleepTime) {
        return WebWorm.getPageJSONArray(sleepTime, "https://videscentrs.lvgmc.lv/data/weather_points_forecast?laiks=" + TimeManager.DATE
                + TimeManager.getCurHour().split(":")[0] + "00&bounds=POLYGON%20((%20" + west + "%20" + south + "%2C%20" + east + "%20"
                + south + "%2C%20" + east + "%20" + north + "%2C%20" + west + "%20" + north + "%2C%20" + west + "%20"
                + south + "))");
    }
    
    //Splits the single Latvia coordinate box into smaller chunks so that all places/points are gotten.
    // int n = resolution = n by n boxes in total: int n = 7 -> resolution = 7x7
    static JSONArray getAllPointsOfResolution(int n, int sleepTime) { // 1 - lowest, 10 - highest resolution
        if (n < 1 || n > 10)
            return null;
        if (n == 1) {
            return scrapeAllForecastPointsFromCoordsNow(LV_WEST, LV_EAST, LV_SOUTH, LV_NORTH, sleepTime);
        } else {
            JSONArray jsonArr = new JSONArray();
            int rowCount = n;
            double rowHeight = (int) ((LV_NORTH - LV_SOUTH) * 10000 / rowCount);
            double west, east, south, north;
            double columnWidth = (int) ((LV_EAST - LV_WEST) * 10000 / rowCount);
            rowHeight = rowHeight / 10000;
            columnWidth = columnWidth / 10000;
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < rowCount; j++) {
                    west = LV_WEST + columnWidth * j;
                    east = LV_WEST + columnWidth * (j + 1);
                    south = LV_SOUTH + rowHeight * i;
                    north = LV_SOUTH + rowHeight * (i + 1);
                    System.out.println("SCRAPING BOX: " +(j+1)+ " x " +(i+1));
                    jsonArr.putAll(scrapeAllForecastPointsFromCoordsNow(west, east, south, north, sleepTime));
                }
            }
            return jsonArr;
        }
    }
    
    // Includes translating a place into a point. Finds the forecast for a place at a specific date/time. 
    // The return value is for displaying in a JTable.
    static String[][] getFullForecastTableArrayForPlaceAndTime(int pointID, String dateTime) {
        System.out.println(pointID);
        JSONArray jsonArr = scrapeHourlyForecastFromPoint(pointID);
        String[][] tableData = {};
        for (Object obj : jsonArr) {
            JSONObject jsonObj = (JSONObject) obj;
            if (jsonObj.get("laiks").equals(dateTime)) {
                tableData = getTableArrayFromJSONObject(jsonObj);
                break;
            }
        }
        return tableData;
    }

    static HashMap<String, String[]> getFullForecastTableArrayForPlace(int pointID) {
        System.out.println(pointID);
        JSONArray jsonArr = scrapeHourlyForecastFromPoint(pointID);
        HashMap<String, String[]> allForecasts = new HashMap<String, String[]>();
        for (Object obj : jsonArr) {
            JSONObject jsonObj = (JSONObject) obj;
            String[] hourForecast = new String[JSON_OBJ_KEYS.length - 2];
            try {
                for (int i = 2; i < JSON_OBJ_KEYS.length; i++) {
                    hourForecast[i-2] = jsonObj.get(JSON_OBJ_KEYS[i]).toString();
                }
                allForecasts.put(jsonObj.get(JSON_OBJ_KEYS[0]).toString(), hourForecast);
            } catch (Exception e) {
                System.err.println("ForecastPointScraper - getFullForecastTableArrayForPlace(): " + e);
            }
        }
        return allForecasts;
    }

    // the point for this can be gotten from the database: Main.db.getPointIDFromName(String townName, String regionName)
    static JSONArray scrapeHourlyForecastFromPoint(int point) {
        if (point < 0) {
            System.out.println("scrapeHourlyForecastFromPoint returned null JSONArray");
            return null;
        }
        return WebWorm.getPageJSONArray(0, "https://videscentrs.lvgmc.lv/data/weather_forecast_for_location_hourly?punkts=P" +point);
    }
}
