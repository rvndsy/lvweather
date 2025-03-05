package LVWeather;

import org.json.JSONArray;

public class WeatherStationScraper {

    private static String weatherMonitoringPointsUrl = "https://videscentrs.lvgmc.lv/data/weather_monitoring_points";
    private static String weatherMonitoringDataRawUrlOld = "https://videscentrs.lvgmc.lv/data/weather_monitoring_data_raw";
    // NOTE: 2025-02-03: This is the new URL "https://videscentrs.lvgmc.lv/data/weather_monitoring_data";
    //                   It has a completely new structure with many more keys...
    private static String weatherMonitoringDataUrl = "https://videscentrs.lvgmc.lv/data/weather_monitoring_data";

    static JSONArray scrapeAllStationMetadata(int sleepTime) {
        return WebWorm.getPageJSONArray(sleepTime, weatherMonitoringPointsUrl);
    }

    static JSONArray scrapeAllStationWeatherDataRawOld(int sleepTime) {
        return WebWorm.getPageJSONArray(sleepTime, weatherMonitoringDataRawUrlOld);
    }

    static JSONArray scrapeAllStationWeatherData(int sleepTime) {
        return WebWorm.getPageJSONArray(sleepTime, weatherMonitoringDataUrl);
    }
}
