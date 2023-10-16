package LVWeather;

import java.awt.Color;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {
	static int WINDOW_HEIGHT = 720;
	static int WINDOW_WIDTH = 1280;
	static int LEFT_BUFFER_SIZE = 15;
	static int LEFT_MAX_COMPONENT_WIDTH = 250;
	static int DATE_BOX_WIDTH = 85;
	static int MAX_BOX_HEIGHT = 25;
	static int TOP_BUFFER_SIZE = 15;
	static int BOTTOM_BUFFER_SIZE = 45;
	static int TOTAL_COMBO_BOX_HEIGHT = MAX_BOX_HEIGHT + TOP_BUFFER_SIZE;
	static int MID_BUFFER_SIZE = 120;
	static int TIME_BOX_WIDTH = 60;
	static int TABLE_HEIGHT = 500;
	static int TABLE_WIDTH = TIME_BOX_WIDTH + DATE_BOX_WIDTH + LEFT_MAX_COMPONENT_WIDTH + (LEFT_BUFFER_SIZE * 3);
	static int TABLE_Y_POS = WINDOW_HEIGHT - BOTTOM_BUFFER_SIZE - TABLE_HEIGHT;
	static String FORECAST_TITLE = "Forecast:";
	static String STATION_TITLE = "Station data:";
	static String FONT_NAME = "Spline sans";

	static DB db = new DB();
	static Container cont;
	static JLabel loading = new JLabel("Stuff is loading . . .     (hopefully)");
	static JFrame frame;
	static Color bgColor = Color.decode("#B3DDFF");

	public static void main(String[] args) {
		// below are methods to execute actions on the database
		// note: populateForecastPointTableDB takes about 10 seconds to finish
		db.truncateForecastPointTable();
		db.truncateWeatherStationMetadataTable();
		// db.truncateWeatherDataTable(); <- Careful with this one
		populateForecastPointTableDB();
		populateWeatherStationMetadataTableDB();
		populateWeatherDataTableDB();

		// The main JFrame is created below.
		frame = new JFrame("LVWeather");
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null); // Centers the window
		frame.setLayout(null);
		frame.setResizable(false);
		cont = frame.getContentPane();
		cont.setBackground(bgColor);
		frame.setVisible(true);

		// Loading screen, why not :/
		loadingScreen(true);

		// Do not change the adding order of townBox1, dateBox1, timeBox1 here (order
		// should be as just stated).
		ForecastComboBox townBox1 = new ForecastComboBox(LEFT_BUFFER_SIZE, TOP_BUFFER_SIZE, LEFT_MAX_COMPONENT_WIDTH,
				MAX_BOX_HEIGHT, db.getTownNameList());
		ForecastComboBox dateBox1 = new ForecastComboBox(townBox1.getWidth() + townBox1.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, DATE_BOX_WIDTH, MAX_BOX_HEIGHT, ForecastPointScraper.getAvailableDatesStringArray());
		ForecastComboBox timeBox1 = new ForecastComboBox(dateBox1.getWidth() + dateBox1.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, TIME_BOX_WIDTH, MAX_BOX_HEIGHT,
				ForecastPointScraper.getAvailableTimesStringArray(TimeManager.getTodaysDate()));
		GetResultsBox getForecastBox = new GetResultsBox(timeBox1.getWidth() + timeBox1.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, 120, MAX_BOX_HEIGHT);
		ForecastComboBox.loadTitle();
		getForecastBox.linkToForecast();
		
		StationComboBox townBox2 = new StationComboBox(WINDOW_WIDTH/2+50, TOP_BUFFER_SIZE, LEFT_MAX_COMPONENT_WIDTH-100,
				MAX_BOX_HEIGHT, db.getStationNameList());
		StationComboBox dateBox2 = new StationComboBox(townBox2.getWidth() + townBox2.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, DATE_BOX_WIDTH, MAX_BOX_HEIGHT, db.getAvailableStationDataDates());
		StationComboBox timeBox2 = new StationComboBox(dateBox2.getWidth() + dateBox2.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, TIME_BOX_WIDTH, MAX_BOX_HEIGHT,
				db.getAvailableTimesForDate(TimeManager.getTodaysDateWithDashes()));
		GetResultsBox getStationBox = new GetResultsBox(timeBox2.getWidth() + timeBox2.getX() + LEFT_BUFFER_SIZE,
				TOP_BUFFER_SIZE, 120, MAX_BOX_HEIGHT);
		StationComboBox.loadTitle();
		getStationBox.linkToStation();
		loadingScreen(false);
		
		cont.add(townBox1);
		cont.add(dateBox1);
		cont.add(timeBox1);
		cont.add(getForecastBox);
		cont.add(townBox2);
		cont.add(dateBox2);
		cont.add(timeBox2);
		cont.add(getStationBox);

		townBox1.updateUI();
		dateBox1.updateUI();
		timeBox1.updateUI();
		townBox2.updateUI();
		dateBox2.updateUI();
		timeBox2.updateUI();
		cont.repaint();
	}

	static void loadingScreen(boolean setLoading) {
		if (setLoading) {
			loading.setBounds(WINDOW_WIDTH / 2 - 300 / 2, WINDOW_HEIGHT / 2 - MAX_BOX_HEIGHT, 300, MAX_BOX_HEIGHT);
			loading.setVisible(true);
			loading.setOpaque(false);
			loading.setBackground(bgColor);
			cont.add(loading);
		} else {
			cont.remove(loading);
		}
	}

	// The populate methods are for ease of reading and not to confuse with
	// db.truncate methods.
	static void populateForecastPointTableDB() {
		db.insertForecastPointIntoTable();
	}

	static void populateWeatherStationMetadataTableDB() {
		db.insertWeatherStationMetadataIntoTable();
	}

	static void populateWeatherDataTableDB() {
		db.insertWeatherDataIntoTable();
	}
}