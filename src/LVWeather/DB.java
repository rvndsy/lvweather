package LVWeather;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.sql.Types;

import org.json.JSONArray;
import org.json.JSONObject;

public class DB {
    private Connection con;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;
    private ResultSet rs2;
    private boolean status = false;

    private static String DB_NAME = "lvweather";
    private static String FORECAST_POINTS_TABLE_TABLE = "forecast_points";
    private static String WEATHER_STATION_METADATA_TABLE_NAME = "weather_station_metadata";
    private static String WEATHER_STATION_DATA_TABLE_NAME = "weather_station_data";

    private static String[] STATION_DATA_TABLE_FIRST_COLUMN = { "Date and time:", "Station name: ", "Temperature: ",
            "Wind direction: ", "Avg wind speed: ", "Wind gusts: ", "Relative humidity: ", "Atmospheric pressure: ",
            "Precipitation (mm/1h): ", "Visibility: ", "Snow cover: ", "UVI Index: " };

    private static String FORECAST_POINT_INSERT_STATEMENT = "INSERT INTO `" + DB_NAME + "`.`"
            + FORECAST_POINTS_TABLE_TABLE + "` (`point_id`, `town_name`, `region_name`, `weight`) VALUES (?, ?, ?, ?)";

    private static String WEATHER_STATION_METADATA_INSERT_STATEMENT = "INSERT INTO `" + DB_NAME + "`.`"
            + WEATHER_STATION_METADATA_TABLE_NAME
            + "` (`station_code`, `station_name`, `longitude`, `latitude`, `elevation`) VALUES (?, ?, ?, ?, ?)";
    // 2025-02-03: Added the `IGNORE` in the SQL statement because of this strange error:
    // GET: https://videscentrs.lvgmc.lv/data/weather_monitoring_data_raw
    // Failed to execute weather_station_data INSERT batch java.sql.BatchUpdateException: Duplicate entry 'RIZO99MS-2024-12-12 15:00:00' for key 'PRIMARY

    //  Old raw table structure
    // private static String WEATHER_STATION_DATA_INSERT_STATEMENT = "INSERT IGNORE INTO `" + DB_NAME + "`.`"
    //         + WEATHER_STATION_DATA_TABLE_NAME + "` "
    //         + "(`date_time`, `station_code`, `temperature`, `wind_direction`, `avg_wind_speed`, `wind_gusts`, `relative_humidity`, `atm_pressure`, "
    //         + "`precipitation_amount`, `visibility`, `snow_cover`, `uvi_index`) VALUES "
    //         + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static String WEATHER_STATION_DATA_INSERT_STATEMENT = "INSERT IGNORE INTO `" + DB_NAME + "`.`"
            + WEATHER_STATION_DATA_TABLE_NAME + "` "
            + "( `date_time`, `station_code`, `clouds`, `air_temperature_min`, `air_temperature_max`, `air_temperature_average`, `air_temperature_actual`, `air_temperature_feel`, `precipitation`, `pressure_sea_level`, `pressure_atmospheric_sea_level`, `pressure_atmospheric_station_level`, `humidity_average`, `humidity_actual`, `snow_average`, `snow_actual`, `wind_direction_average`, `wind_direction_max`, `wind_direction_actual`, `wind_speed_average`, `wind_speed_maximum`, `wind_speed_actual`, `wind_speed_gust`, `lightning_max`, `lightning_cloud`, `lightning_ground`, `lightning_total`, `uvi`, `visibility_actual`, `visibility_average`, `description` )"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static String[] WEATHER_STATION_DATA_COLUMN_NAMES = { "date_time", "station_code", "clouds", "air_temperature_min", "air_temperature_max", "air_temperature_average", "air_temperature_actual", "air_temperature_feel", "precipitation", "pressure_sea_level", "pressure_atmospheric_sea_level", "pressure_atmospheric_station_level", "humidity_average", "humidity_actual", "snow_average", "snow_actual", "wind_direction_average", "wind_direction_max", "wind_direction_actual", "wind_speed_average", "wind_speed_maximum", "wind_speed_actual", "wind_speed_gust", "lightning_max", "lightning_cloud", "lightning_ground", "lightning_total", "uvi", "visibility_actual", "visibility_average", "description" };

    /*
     * private static String HOURLY_FORECAST_FOR_POINT_INSERT_STATEMENT =
     * "INSERT INTO `" + DB + "`.`" + HOURLY_FORECAST_FOR_POINT_TABLE_NAME +
     * "` (`date_time`, `point_id`, `name`, `temperature`, `temperature_feelslike`, `wind_speed`, `wind_direction`, `wind_gusts`, `precipitation_1hour`, "
     * +
     * "`precipitation_probability`, `relative_humidity`, `atm_pressure`, `snow_cover`, `uvi_index`, `thunderstorm_probability`, `displayed_icon`) "
     * + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
     */
    public DB() {
        try {
            String params = "?useSSL=false&autoReconnect=true&allowMultiQueries=true";
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DB_NAME + params, "root", "root");
            this.stmt = con.createStatement();
            System.out.println("DB connection established");
            status = true;
        } catch (Exception e) {
            System.err.println("Problems creating DB connection");
            e.printStackTrace();
            status = false;
        }
    }

    public boolean isConnected() {
        return status;
    }

    public String[] getAvailableStationDataDates() {
        // https://stackoverflow.com/questions/5335735/selecting-distinct-dates-from-datetime-column-in-a-table
        String query = "SELECT DISTINCT CAST(`date_time` AS DATE) AS dateonly FROM " + WEATHER_STATION_DATA_TABLE_NAME
                + " ORDER BY date_time ASC;";
        rs = select(query);
        ArrayList<String> dateList = new ArrayList<String>();
        try {
            while (rs.next()) {
                dateList.add(TimeManager.formatDateFromDashesToDots(rs.getString("dateonly")));
            }
        } catch (Exception e) {
            System.out.println("Error in getStationNameList DB request " + e);
        }
        Collections.sort(dateList, String.CASE_INSENSITIVE_ORDER.reversed());
        return dateList.toArray(new String[dateList.size()]);
    }

    public String[] getAvailableTimesForDate(String date) {
        String query = "SELECT DISTINCT CAST(`date_time` AS TIME) AS timeonly FROM " + WEATHER_STATION_DATA_TABLE_NAME
                + " WHERE CAST(`date_time` AS DATE) LIKE '" + date + "';";
        rs = select(query);
        System.out.println(query);
        ArrayList<String> timeList = new ArrayList<String>();
        try {
            while (rs.next()) {
                String time = TimeManager.formatDateFromDashesToDots(rs.getString("timeonly"));
                timeList.add(time.substring(0, 5));
            }
        } catch (Exception e) {
            System.out.println("Error in getStationNameList DB request " + e);
        }
        Collections.sort(timeList, String.CASE_INSENSITIVE_ORDER);
        return timeList.toArray(new String[timeList.size()]);
    }

    public String getStationCodeFromName(String stationName) {
        String query = ("SELECT station_code FROM " + WEATHER_STATION_METADATA_TABLE_NAME + " WHERE station_name LIKE '" +stationName+ "';");
        System.out.println(query);
        rs2 = select(query);
        try {    
            rs2.next();
            return rs2.getString(1);
        } catch (Exception e) {
            System.out.println("Error in getStationNameFromCode() DB request " +e);
            return "null";
        }
    }

    public HashMap<String, String[]> getStationDataForDateAndPlace(String stationName, String dateTime) {
        if (!dateTime.contains(".") || !dateTime.contains(":")) {
            dateTime = TimeManager.formatToDBDateTime(dateTime);
        }

        // ignoring dateTime for now...
        //String query = ("SELECT * FROM " + WEATHER_STATION_DATA_TABLE_NAME + " WHERE DATE(date_time) = '" +dateTime+ "' AND station_code LIKE '" +getStationCodeFromName(stationName)+ "';");
        String query = ("SELECT * FROM " + WEATHER_STATION_DATA_TABLE_NAME + " WHERE station_code LIKE '" +getStationCodeFromName(stationName)+ "' ORDER BY date_time ASC;");
        System.out.println(query);
        rs = select(query);

        HashMap<String, String[]> dateData = new HashMap<String, String[]>();

        try {
            while (rs.next()) {
                try {
                    String selectedDateTime = rs.getString(1);
                    String[] selectedDateTimeData = new String[29];
                    for (int i = 3; i < 31; i++) {
                        String secondColumn = null;
                        try {
                            secondColumn = rs.getString(i);
                        } catch (Exception e) {
                            secondColumn = "null";
                        }
                        selectedDateTimeData[i-3] = secondColumn;
                    }
                    System.out.println(TimeManager.formatToRawDateTime(selectedDateTime.replace("T", " ")));
                    dateData.put(TimeManager.formatToRawDateTime(selectedDateTime.replace("T", " ")), selectedDateTimeData);
                } catch (Exception e) {
                    System.out.println("Error in getStationDataForDatetimeAndPlace() DB request " + e);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getStationDataForDatetimeAndPlace() rs.next() " + e);
            return null;
        }
        return dateData;
    }

    public String[] getStationNameList() {
        String query = "SELECT * FROM " + WEATHER_STATION_METADATA_TABLE_NAME + ";";
        rs = select(query);
        ArrayList<String> stationNameList = new ArrayList<String>();
        try {
            while (rs.next()) {
                stationNameList.add(rs.getString("station_name"));
            }
        } catch (Exception e) {
            System.out.println("Error in getStationNameList DB request " + e);
        }
        Collections.sort(stationNameList, String.CASE_INSENSITIVE_ORDER);
        return stationNameList.toArray(new String[stationNameList.size()]);
    }

    public String[] getTownNameList() {
        String query = "SELECT * FROM " + FORECAST_POINTS_TABLE_TABLE + ";";
        rs = select(query);
        ArrayList<String> townNameList = new ArrayList<String>();
        try {
            while (rs.next()) {
                townNameList.add(rs.getString("town_name") + ", " + rs.getString("region_name"));
            }
        } catch (Exception e) {
            System.out.println("Error in getTownNameList DB request " + e);
        }
        Collections.sort(townNameList, String.CASE_INSENSITIVE_ORDER);
        return townNameList.toArray(new String[townNameList.size()]);
    }

    public int getPointIDFromName(String townName, String regionName) {
        String query = "SELECT * FROM " + FORECAST_POINTS_TABLE_TABLE + " WHERE town_name = '" + townName
                + "' AND region_name = '" + regionName + "';";
        System.out.println(query);
        rs = select(query);
        try {
            while (rs.next()) { // iterates every row
                return rs.getInt("point_id"); // returns the first value from first row
            }
        } catch (Exception e) {
            System.out.println("Error in getPointID DB request " + e);
        }
        System.out.println("No such place '" + townName + "'");
        return -1;
    }

    public void insertForecastPointIntoTable(JSONArray jsonArray) {
        try {
            ps = con.prepareStatement(FORECAST_POINT_INSERT_STATEMENT);
        } catch (Exception e) {
            System.out.println("Failed to prepare " + FORECAST_POINTS_TABLE_TABLE + " INSERT statement: " + e);
        }
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            int pointID = Integer.parseInt(jsonObj.get("punkts").toString().replace("P", ""));
            String townName = jsonObj.get("nosaukums").toString();
            String regionName = jsonObj.get("novads").toString();
            int weight = jsonObj.getInt("svars");
            try {
                ps.setInt(1, pointID);
                ps.setString(2, townName);
                ps.setString(3, regionName);
                ps.setInt(4, weight);
                ps.addBatch();
            } catch (Exception e) {
                System.out.println("Failed to insert entry into DB table " + FORECAST_POINTS_TABLE_TABLE + ": " + e);
            }
        }
        try {
            ps.executeBatch();
        } catch (Exception e) {
            System.out.println("Failed to execute" + FORECAST_POINTS_TABLE_TABLE + " INSERT batch: " + e);
        }
    }

    public void insertWeatherStationMetadataIntoTable(JSONArray jsonArray) {
        try {
            ps = con.prepareStatement(WEATHER_STATION_METADATA_INSERT_STATEMENT);
        } catch (Exception e) {
            System.out.println("Failed to prepare WeatherStationInfo INSERT statement "
                    + WEATHER_STATION_METADATA_TABLE_NAME + ": " + e);
        }
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            String stationCode = jsonObj.get("kods").toString();
            String stationName = jsonObj.get("nosaukums").toString();
            double longitude = jsonObj.getDouble("lon");
            double latitude = jsonObj.getDouble("lat");
            double height = jsonObj.getDouble("h");
            try {
                ps.setString(1, stationCode);
                ps.setString(2, stationName);
                ps.setDouble(3, longitude);
                ps.setDouble(4, latitude);
                ps.setDouble(5, height);
                ps.addBatch();
            } catch (Exception e) {
                System.out.println(
                        "Failed to insert entry into DB table " + WEATHER_STATION_METADATA_TABLE_NAME + ": " + e);
            }
        }
        try {
            ps.executeBatch();
        } catch (Exception e) {
            System.out.println("Failed to execute " + WEATHER_STATION_METADATA_TABLE_NAME + " INSERT batch " + e);
        }
    }

    public void insertWeatherDataIntoTable(JSONArray jsonArray) {
        //String[] jsonFloatKeysOld = { "temperatura", "veja_virziens", "videja_veja_atrums", "veja_brazmas",
        //        "relativais_mitrums", "atmosferas_spiediens", "nokrisnu_daudzums", "redzamiba", "sniega_segas_biezums",
        //        "uvi" };
        String[] jsonFloatKeys = { "clouds", "air_temperature_min", "air_temperature_max", "air_temperature_average",
        "air_temperature_actual", "air_temperature_feel", "precipitation", "pressure_sea_level", "pressure_atmospheric_sea_level",
        "pressure_atmospheric_station_level", "humidity_average", "humidity_actual", "snow_average", "snow_actual",
        "wind_direction_average", "wind_direction_max", "wind_direction_actual", "wind_speed_average", "wind_speed_maximum", "wind_speed_actual", "wind_speed_gust",
        "lightning_max", "lightning_cloud", "lightning_ground", "lightning_total", "uvi", "visibility_actual",
        "visibility_average", "description", };

        // DB DATE FORMAT = 2023-05-01 19:58:46
        try {
            ps = con.prepareStatement(WEATHER_STATION_DATA_INSERT_STATEMENT);
        } catch (Exception e) {
            System.out.println("Failed to prepare WeatherStationInfo INSERT statement "
                    + WEATHER_STATION_DATA_TABLE_NAME + ": " + e);
        }
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            LocalDateTime dateTime = LocalDateTime.parse(jsonObj.get("time").toString().replace("T", " "), TimeManager.formatter);
            String stationCode = jsonObj.get("station_code").toString();
            String description = jsonObj.get("description").toString();
            try {

                ps.setObject(1, dateTime);
                ps.setString(2, stationCode);
                if (description == null || description.equals("null")) {
                    ps.setObject(31, null);
                } else {
                    ps.setString(31, description);
                }

                for (int i = 0; i < jsonFloatKeys.length - 1; i++) {
                    String keyValueString = jsonObj.get(jsonFloatKeys[i]).toString();
                    if (!keyValueString.equals("null")) {
                        float keyValueFloat = Float.parseFloat(keyValueString);
                        ps.setFloat(i + 3, keyValueFloat);
                    } else {
                        ps.setNull(i + 3, Types.FLOAT);
                    }
                }
                ps.addBatch();

            } catch (Exception e) {
                System.out
                        .println("Failed to insert entry into DB table " + WEATHER_STATION_DATA_TABLE_NAME + ": " + e);
            }
        }
        try {
            ps.executeBatch();
        } catch (Exception e) {
            System.out.println("Failed to execute " + WEATHER_STATION_DATA_TABLE_NAME + " INSERT batch " + e);
        }
    }


    public void truncateForecastPointTable() {
        truncateTable(FORECAST_POINTS_TABLE_TABLE);
    }

    public void truncateWeatherStationMetadataTable() {
        truncateTable(WEATHER_STATION_METADATA_TABLE_NAME);
    }

    public void truncateWeatherDataTable() {
        truncateTable(WEATHER_STATION_DATA_TABLE_NAME);
    }
    
    public void truncateTable(String table) {
        String query = "TRUNCATE TABLE " + table + ";";
        try {
            stmt.executeUpdate(query);
            System.out.println("Data in table " + table + " deleted successfully");
        } catch (Exception e) {
            System.out.println("Failed to execute " + table + " TRUNCATE TABLE: " + e);
        }
    }

    public ResultSet select(String query) {
        try {
            return stmt.executeQuery(query);
        } catch (Exception e) {
            System.err.println("Problems with query: " + query);
            e.printStackTrace();
            return null;
        }
    }

    public void insert(String query) {
        try {
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.err.println("Problems with query: " + query);
            e.printStackTrace();
        }
    }

    // //
    // //  Data formatting
    // //

    // public String[] prepareStationRequest(String townName, String date, String time) {
    //     String dateTime = TimeManager.formatToDBDateTime(date, time);
    // }
}
