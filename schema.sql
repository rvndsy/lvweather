CREATE DATABASE IF NOT EXISTS lvweather;

CREATE TABLE IF NOT EXISTS lvweather.forecast_points (
  `point_id` int unsigned NOT NULL COMMENT 'One-to-one assigned point to a place in Latvia',
  `town_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT 'Names of place',
  `region_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT 'Region/municipality of place',
--  `town_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Names of place',
--  `region_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Region/municipality of place',
  `weight` int unsigned DEFAULT NULL,
  PRIMARY KEY (`point_id`)
);

CREATE TABLE IF NOT EXISTS lvweather.hourly_forecast_for_point (
  `date_time` datetime DEFAULT NULL,
  `point_id` int unsigned DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
--  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `temperature` float DEFAULT NULL,
  `temperature_feelslike` float DEFAULT NULL,
  `wind_speed` float DEFAULT NULL,
  `wind_direction` float DEFAULT NULL,
  `wind_gusts` float DEFAULT NULL,
  `precipitation_1hour` float DEFAULT NULL,
  `precipitation_probability` float DEFAULT NULL,
  `relative_humidity` float DEFAULT NULL,
  `atm_pressure` float DEFAULT NULL,
  `snow_cover` float DEFAULT NULL,
  `uvi_index` float DEFAULT NULL,
  `thunderstorm_probability` float DEFAULT NULL,
  `displayed_icon` int DEFAULT NULL COMMENT 'Probably useless'
);
-- NOTE: Add a primary ID that isnt the station_code? Same everywhere else?
CREATE TABLE IF NOT EXISTS `weather_station_metadata` (
  `station_code` varchar(10) CHARACTER SET utf8 NOT NULL,
  `station_name` varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `elevation` double DEFAULT NULL,
  PRIMARY KEY (`station_code`)
);

CREATE TABLE IF NOT EXISTS `weather_station_data` (
  `date_time` datetime NOT NULL,
  `station_code` varchar(10) CHARACTER SET utf8 NOT NULL,
  `clouds` float DEFAULT NULL,
  `air_temperature_min` float DEFAULT NULL,
  `air_temperature_max` float DEFAULT NULL,
  `air_temperature_average` float DEFAULT NULL,
  `air_temperature_actual` float DEFAULT NULL,
  `air_temperature_feel` float DEFAULT NULL,
  `precipitation` float DEFAULT NULL,
  `pressure_sea_level` float DEFAULT NULL,
  `pressure_atmospheric_sea_level` float DEFAULT NULL,
  `pressure_atmospheric_station_level` float DEFAULT NULL,
  `humidity_average` float DEFAULT NULL,
  `humidity_actual` float DEFAULT NULL,
  `snow_average` float DEFAULT NULL,
  `snow_actual` float DEFAULT NULL,
  `wind_direction_average` float DEFAULT NULL,
  `wind_direction_max` float DEFAULT NULL,
  `wind_direction_actual` float DEFAULT NULL,
  `wind_speed_average` float DEFAULT NULL,
  `wind_speed_maximum` float DEFAULT NULL,
  `wind_speed_actual` float DEFAULT NULL,
  `wind_speed_gust` float DEFAULT NULL,
  `lightning_max` float DEFAULT NULL,
  `lightning_cloud` float DEFAULT NULL,
  `lightning_ground` float DEFAULT NULL,
  `lightning_total` float DEFAULT NULL,
  `uvi` float DEFAULT NULL,
  `visibility_actual` float DEFAULT NULL,
  `visibility_average` float DEFAULT NULL,
  `description` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`station_code`,`date_time`)
);


-- Old raw data table
-- `station_code` varchar(10) CHARACTER SET utf8 NOT NULL,
-- `temperature` float DEFAULT NULL,
-- `wind_direction` float DEFAULT NULL,
-- `avg_wind_speed` float DEFAULT NULL,
-- `wind_gusts` float DEFAULT NULL,
-- `relative_humidity` float DEFAULT NULL,
-- `atm_pressure` float DEFAULT NULL,
-- `precipitation_amount` float DEFAULT NULL,
-- `visibility` float DEFAULT NULL,
-- `snow_cover` float DEFAULT NULL,
-- `uvi_index` float DEFAULT NULL,
