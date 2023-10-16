package LVWeather;

import org.json.JSONArray;

public class WeatherStationScraper {
	
	private static String weatherMonitoringPointsUrl = "https://videscentrs.lvgmc.lv/data/weather_monitoring_points";
	private static String weatherMonitoringDataRawUrl = "https://videscentrs.lvgmc.lv/data/weather_monitoring_data_raw";

	static JSONArray scrapeAllStationMetadata() {
		return WebWorm.getPageJSONArray(250, weatherMonitoringPointsUrl);
	}
	
	static JSONArray scrapeAllStationWeatherData() {
		return WebWorm.getPageJSONArray(250, weatherMonitoringDataRawUrl);
	}
}