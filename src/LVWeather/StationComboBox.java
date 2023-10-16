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

public class StationComboBox extends JComboBox<String> implements ActionListener{
	private static final long serialVersionUID = 1L; // Auto-generated thing
	
	private static JLabel titleLabel;
	private static TableElement activeTable;
	private static ArrayList<StationComboBox> allComboBoxes = new ArrayList<StationComboBox>();
	private static boolean isWorking = false;

	public StationComboBox(int x, int y, int width, int height, String[] contentList) {
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
        titleLabel.setText(Main.STATION_TITLE);
        titleLabel.setVisible(true);
        titleLabel.setOpaque(false);
        titleLabel.setBounds(Main.WINDOW_WIDTH/2+50, Main.TABLE_Y_POS-60, Main.TABLE_WIDTH, 30);
        titleLabel.setFont(new Font(Main.FONT_NAME, Font.BOLD, 32));
        Main.cont.add(titleLabel);
	}
	
	//When date combobox value is selected, the time combobox values are updated.
	public void actionPerformed(ActionEvent e) {// from
												// https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
		StationComboBox clickedLabel = null;
		try {
			clickedLabel = (StationComboBox) e.getSource();
		} catch (Exception e2) {
			System.out.println("Error in StationComboBox actionPerformed(): " + e2);
		}
		try {
			StationComboBox dateBox2 = allComboBoxes.get(1);
			StationComboBox timeBox2 = allComboBoxes.get(2);
			if (clickedLabel.equals(dateBox2)) {
				timeBox2.removeAllItems();
				for (String str : Main.db.getAvailableTimesForDate(TimeManager.formatDateFromDotsToDashes(dateBox2.getSelectedItem().toString()))) {
					timeBox2.addItem(str);
				}
			} else {
				return;
			}
		} catch (Exception e2) {
		}
	}

	//Once getResultsBoxClicked() results box is clicked, data from the comboboxes is retrieved.
	public static void getResultsBoxClicked() {
		if (isWorking()) {
			return;
		}
		startingWork();
		String townName = String.valueOf(allComboBoxes.get(0).getSelectedItem()); // https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
		String date = String.valueOf(allComboBoxes.get(1).getSelectedItem());
		String time = String.valueOf(allComboBoxes.get(2).getSelectedItem());
		displayForecast(townName, date, time);
	}

	//Runs a new thread to display the table, program feels less sluggish.
	private static void displayForecast(String townName, String date, String time) {
		String dateTime = TimeManager.formatToDBDateTime(date, time);
		Thread t = new Thread() {
			public void run() {
				try {
					try {
						Main.cont.remove(activeTable);
					} catch (Exception e) {
					}
					String[][] tableData = Main.db.getStationDataForDatetimeAndPlace(townName, dateTime);
					activeTable = new TableElement(Main.WINDOW_WIDTH/2+50, Main.TABLE_Y_POS, Main.TABLE_WIDTH, Main.TABLE_HEIGHT,
							tableData, "Station data:");
					Main.cont.add(activeTable);
					activeTable.updateUI();
					pause(250);
					endingWork();
				} catch (Exception e) {
					System.out.println("Thread (" + Thread.currentThread().getId()
							+ "): Error in StationComboBox displayForecast(): " + e);
				}
			};
		};
		t.start();
	}

	//3 methods below are for only letting 1 displayForecast() thread run.
	static boolean startingWork() {
		isWorking = true;
		return isWorking;
	}

	static boolean endingWork() {
		isWorking = false;
		return isWorking;
	}

	static boolean isWorking() {
		return isWorking;
	}

	static void pause(int sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (Exception e) {
			System.out.println("Thread problems: " + e);
		}
	}
}
