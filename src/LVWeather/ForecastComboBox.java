package LVWeather;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class ForecastComboBox extends JComboBox<String> implements ActionListener{
	private static final long serialVersionUID = 1L; // Auto-generated thing
	
	private static JLabel titleLabel;
	private static TableElement activeTable;
	private static ArrayList<ForecastComboBox> allComboBoxes = new ArrayList<ForecastComboBox>();
	private static boolean runningWebScrape = false;

	public ForecastComboBox(int x, int y, int width, int height, String[] contentList) {
		super(contentList);
		setVisible(true);
		setBounds(x, y, width, height);
		setEditable(true);
		AutoCompleteDecorator.decorate(this);
		updateUI();
		addActionListener(this);
		allComboBoxes.add(this);
	}
	
	static void loadTitle() {
        titleLabel = new JLabel();
        titleLabel.setText(Main.FORECAST_TITLE);
        titleLabel.setVisible(true);
        titleLabel.setOpaque(false);
        titleLabel.setBounds(Main.LEFT_BUFFER_SIZE, Main.TABLE_Y_POS-60, Main.TABLE_WIDTH, 30);
        titleLabel.setFont(new Font(Main.FONT_NAME, Font.BOLD, 32));
        Main.cont.add(titleLabel);
	}
	
	//When date combobox value is selected, the time combobox values are updated.
	public void actionPerformed(ActionEvent e) {// from
												// https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
		ForecastComboBox clickedLabel = null;
		try {
			clickedLabel = (ForecastComboBox) e.getSource();
		} catch (Exception e2) {
			System.out.println("Error in ForecastComboBox actionPerformed(): " + e2);
		}
		try {
			ForecastComboBox dateBox1 = allComboBoxes.get(1);
			ForecastComboBox timeBox1 = allComboBoxes.get(2);
			if (clickedLabel.equals(dateBox1)) {
				timeBox1.removeAllItems();
				for (String str : ForecastPointScraper.getAvailableTimesStringArray(dateBox1.getSelectedItem().toString())) {
					timeBox1.addItem(str);
				}
			} else {
				return;
			}
		} catch (Exception e2) {
		}
	}

	//Once getResultsBoxClicked() results box is clicked, data from the comboboxes is retrieved.
	public static void getResultsBoxClicked() {
		if (isWebScraping()) {
			return;
		}
		startingWebScraping();
		String townFullName = String.valueOf(allComboBoxes.get(0).getSelectedItem()); // https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
		String date = String.valueOf(allComboBoxes.get(1).getSelectedItem());
		String time = String.valueOf(allComboBoxes.get(2).getSelectedItem());
		prepareForecast(townFullName, date, time);
	}

	public static void prepareForecast(String townFullName, String date, String time) {
		String dateTime = TimeManager.formatToRawDateTime(date, time);
		if (townFullName.contains(", ") && townFullName.split(", ").length == 2) {
			String townName = townFullName.split(", ")[0];
			String regionName = townFullName.split(", ")[1];
			displayForecast(townName, regionName, dateTime);
		}
	}
	
	//Runs a new thread to display the table, program feels less sluggish.
	private static void displayForecast(String townName, String regionName, String dateTime) {
		Thread t = new Thread() {
			public void run() {
				try {
					try {
						Main.cont.remove(activeTable);
					} catch (Exception e) {
					}
					String[][] tableData = ForecastPointScraper.getFullForecastTableArrayForPlaceAndTime(townName,
							regionName, dateTime);
					activeTable = new TableElement(Main.LEFT_BUFFER_SIZE, Main.TABLE_Y_POS, Main.TABLE_WIDTH, Main.TABLE_HEIGHT,
							tableData, "Forecast");
					Main.cont.add(activeTable);
					activeTable.updateUI();
					pause(250);
					endingWebScraping();
				} catch (Exception e) {
					System.out.println("Thread (" + Thread.currentThread().getId()
							+ "): Error in ComboBox displayForecast(): " + e);
				}
			};
		};
		t.start();
	}
	
	//3 methods below are for only letting 1 displayForecast() thread run.
	static boolean startingWebScraping() {
		runningWebScrape = true;
		return runningWebScrape;
	}

	static boolean endingWebScraping() {
		runningWebScrape = false;
		return runningWebScrape;
	}

	static boolean isWebScraping() {
		return runningWebScrape;
	}

	static void pause(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
			System.out.println("Thread problems: " + e);
		}
	}
}
