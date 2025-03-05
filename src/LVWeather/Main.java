package LVWeather;

import org.json.JSONArray;

public class Main {
    public static void main(String[] args) {

        final int SLEEP_TIME = 100;

        DB db = new DB();
        if (!db.isConnected()) {
            System.out.println("Initial database connection failed! Exiting...");
            System.exit(1);
        }
        // below are methods to execute actions on the database
        // note: populateForecastPointTableDB takes about SLEEP_TIME * 36 milliseconds to finish
        //db.truncateForecastPointTable();
        //db.truncateWeatherStationMetadataTable();
        ////db.truncateWeatherDataTable(); <- Careful with this one, it will wipe all data including which CANNOT BE RETRIEVED from here anymore
        //populateForecastPointTableDB(db, SLEEP_TIME);
        //populateWeatherStationMetadataTableDB(db, SLEEP_TIME);
        populateWeatherDataTableDB(db, SLEEP_TIME);

        GUI gui = new GUI("LVWeather", db);
    }

    // The populate methods are for ease of reading and not to confuse with
    // db.truncate methods.
    static void populateForecastPointTableDB(DB db, int sleepTime) {
        JSONArray jsonArray = ForecastPointScraper.getAllPointsOfResolution(6, sleepTime);
        db.insertForecastPointIntoTable(jsonArray);
    }

    static void populateWeatherStationMetadataTableDB(DB db, int sleepTime) {
        JSONArray jsonArray = WeatherStationScraper.scrapeAllStationMetadata(sleepTime);
        db.insertWeatherStationMetadataIntoTable(jsonArray);
    }

    static void populateWeatherDataTableDB(DB db, int sleepTime) {
        JSONArray jsonArray = WeatherStationScraper.scrapeAllStationWeatherData(sleepTime);
        db.insertWeatherDataIntoTable(jsonArray);
    }
}
