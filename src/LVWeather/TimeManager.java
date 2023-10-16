package LVWeather;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeManager {
	//This class exists to reduce the amount of pain when dealing with the date/time Strings.
	static String DATE = LocalDate.now().toString().replace("-", "");
	static String TIME = LocalTime.now().toString();
	static String DATE_TIME = DATE + TIME.split(":")[0] + "00";
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
	//TIME_HOUR_LIST not needed anymore
	/*static String[] TIME_HOUR_LIST = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", 
			 "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
			 "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
			 "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",};
	*/
	
	static String formatRawDateTime(String str) {
		return str.substring(0, 4) + "." + str.substring(4, 6) +"."+ str.substring(6, 8) 
		+" "+ str.substring(8, 10) +":"+ str.substring(10, 12);
	}
	
	static String formatToRawDateTime(String date, String time) {
		return date.replace(".", "").strip() + time.replace(":", "").strip();
	}
	
	static String formatToDBDateTime(String date, String time) {
		return date.replace(".", "-").strip() + " " + time.strip() +":00";
	}
	
	static String formatToRawDate(String date) {
		return date.replace(".", "").strip();
	}
	
	static String formatDateFromDashesToDots(String date) {
		return date.replace("-", ".").strip();
	}
	
	static String formatDateFromDotsToDashes(String date) {
		return date.replace(".", "-").strip();
	}
	
	static String getTodaysDate() {
		return DATE;
	}
	
	static String getTodaysDateWithDashes() {
		return LocalDate.now().toString();
	}
}