package LVWeather;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GUI {

    final static int WINDOW_HEIGHT = 720;
    final static int WINDOW_WIDTH = 1280;
    final static int LEFT_BUFFER_SIZE = 15;
    final static int LEFT_MAX_COMPONENT_WIDTH = 250;
    final static int DATE_BOX_WIDTH = 85;
    final static int MAX_BOX_HEIGHT = 25;

    final static int LEFT_PANE_WIDTH = 250;
    final static int LEFT_PANE_BUTTON_HEIGHT = MAX_BOX_HEIGHT;
    final static int LEFT_PANE_SMALL_BUTTON_WIDTH = 105; // 2 small buttons per row
    final static int LEFT_PANE_LARGE_BUTTON_WIDTH = 220; // 1 large button per row

    final static int TABLE_ROW_HEIGHT = 20;
    final static int TABLE_FONT_SIZE = 12;
    final static int TABLE_HEIGHT = WINDOW_HEIGHT - 100;
    final static int TABLE_WIDTH = 480;

    final static Color BG_COLOR = Color.decode("#FFFFFF");
    final static String FONT_NAME = "Spline sans";
    final static Font BIG_FONT = new Font(FONT_NAME, Font.PLAIN, 32);
    final static Font COMBOBOX_FONT = new Font(FONT_NAME, Font.BOLD, 12);
    final static int CENTER_PANE_WIDTH = 400;

    public static String DEFAULT_TOWN = "Rīga, Rīgas pilsēta";
    public static String DEFAULT_STATION_TOWN = "Ainaži";
    // Date and time is excluded
    private static String[] FORECAST_TABLE_FIRST_COLUMN = {"Temperature: ", "'Feels like': ", "Wind speed: ", "Wind gusts: ", "Wind direction: "
            , "Precipitation (mm/1h): ","Precipitation probability: ", "Snow: ", "Thunderstorms: ", "Relative humidity: ",
            "Atmospheric pressure: ", "Cloud cover: ", "UVI Index: " };
    private static String[] STATION_TABLE_FIRST_COLUMN = { "clouds", "air_temperature_min", "air_temperature_max", "air_temperature_average",
        "air_temperature_actual", "air_temperature_feel", "precipitation", "pressure_sea_level", "pressure_atmospheric_sea_level",
        "pressure_atmospheric_station_level", "humidity_average", "humidity_actual", "snow_average", "snow_actual",
        "wind_direction_average", "wind_direction_max", "wind_direction_actual", "wind_speed_average", "wind_speed_maximum", "wind_speed_actual", "wind_speed_gust",
        "lightning_max", "lightning_cloud", "lightning_ground", "lightning_total", "uvi", "visibility_actual",
        "visibility_average", "description", };

    // Global UI elements
    private Container cont;
    private JLabel loading = new JLabel("Stuff is loading . . .     (hopefully)");
    private JFrame frame;
    private JTabbedPane tabbedFrame;

    // Forecast pane
    private JPanel fTab;
    private JPanel fCenter;
    private ButtonPanel fLeft;
    private TableElement fTable;

    // Station pane
    private JPanel sTab;
    private JPanel sCenter;
    private ButtonPanel sLeft;
    private TableElement sTable;

    // Non-UI variables
    private DB db;
    private StationDataStorage stationDataStorage;
    private ForecastDataStorage forecastDataStorage;
    private boolean isForecastDataWorkRunning = false;
    private boolean isStationDataWorkRunning = false;

    public GUI(String windowName, DB db) {
        setDB(db);
        //
        // Initializing the base JFrame for all GUI elements
        //
        frame = new JFrame(windowName);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Centers the window
        frame.setResizable(false);
        frame.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        cont = frame.getContentPane();
        cont.setBackground(BG_COLOR);
        frame.setVisible(true);

        tabbedFrame = new JTabbedPane(JTabbedPane.TOP);

        forecastDataStorage = new ForecastDataStorage(db, DEFAULT_TOWN, TimeManager.getCurDate(), TimeManager.getNextHour());
        stationDataStorage = new StationDataStorage(db, DEFAULT_STATION_TOWN, TimeManager.getCurDate(), TimeManager.getNextHour());

        // Loading screen... because why not :/
        loadingScreen(true);

        //db.getStationDataForDateAndPlace("Ainaži", "2025-02-23T12:00:00");
        //System.exit(1);

        //
        // Continue setting up the GUI layout...
        //
        //      Forecast...
        initForecastPanels();
        //      Station...
        initStationPanels();

        tabbedFrame.addTab("Forecast", fTab);
        tabbedFrame.addTab("Station data", sTab);

        cont.add(tabbedFrame);

        loadingScreen(false);
        frame.setVisible(true);

        cont.repaint();
    }

    private void initTabPagePanel(JPanel pagePanel) {
        pagePanel.setLayout(new BorderLayout());
    }

    private void initCenterPanel(JPanel centerPanel) {
        centerPanel.setPreferredSize(new Dimension(CENTER_PANE_WIDTH, WINDOW_HEIGHT - 50));
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
    }

    private void initStationComboBoxData() {
        sLeft.updateTownComboBox(db.getStationNameList(), DEFAULT_STATION_TOWN);
        sLeft.updateDateComboBox(stationDataStorage.getAvailableDatesStringArray(), null);
        sLeft.updateTimeComboBox(stationDataStorage.getAvailableTimesStringArray(TimeManager.formatToRawDate(sLeft.getDateSelectedItem())), null);
    }

    private void setUpStationEventListeners() {
        //sLeft.addTownBoxEventListener();
        //sLeft.addTimeBoxEventListener();
        sLeft.addDateBoxEventListener(this::updateStationComboBoxes);
        sLeft.addGetResultsEventListener(this::onGetStationResultsActionEvent);
        //sLeft.addTimeLeftEventListener(this::onStationBackwardActionEvent);
        //sLeft.addTimeRightEventListener(this::onStationForwardActionEvent);
    }

    private void initStationPanels() {
        sTab = new JPanel();
        initTabPagePanel(sTab);

        sCenter = new JPanel();
        initCenterPanel(sCenter);

        sLeft = new ButtonPanel();
        initStationComboBoxData();

        // NOTE: Left and Right buttons are disabled for Station data for now!!!

        sTab.add(sLeft, BorderLayout.WEST);
        sTab.add(sCenter, BorderLayout.CENTER);

        setUpStationEventListeners();
    }

    private void initForecastComboBoxData() {
        fLeft.updateTownComboBox(db.getTownNameList(), DEFAULT_TOWN);
        fLeft.updateDateComboBox(forecastDataStorage.getAvailableDatesStringArray(), null);
        fLeft.updateTimeComboBox(forecastDataStorage.getAvailableTimesStringArray(TimeManager.formatToRawDate(fLeft.getDateSelectedItem())), null);
    }

    private void setUpForecastEventListeners() {
        //fLeft.addTownBoxEventListener();
        //fLeft.addTimeBoxEventListener();
        fLeft.addDateBoxEventListener(this::onfDateBoxActionEvent);
        fLeft.addGetResultsEventListener(this::onGetForecastResultsActionEvent);
        fLeft.addTimeLeftEventListener(this::onForecastLeftActionEvent);
        fLeft.addTimeRightEventListener(this::onForecastRightActionEvent);
    }

    private void initForecastPanels() {
        fTab = new JPanel();
        initTabPagePanel(fTab);

        fCenter = new JPanel();
        initCenterPanel(fCenter);

        fLeft = new ButtonPanel();
        initForecastComboBoxData();

        fTab.add(fLeft, BorderLayout.WEST);
        fTab.add(fCenter, BorderLayout.CENTER);

        setUpForecastEventListeners();
    }

    public void onGetForecastResultsActionEvent() {
            System.out.println("[BUTTON EVENT] Forecast GET RESULT button clicked!");
            getForecastResultsBoxClicked();
    }

    public void onGetStationResultsActionEvent() {
            System.out.println("[BUTTON EVENT] Station GET RESULT button clicked!");
            getStationResultsBoxClicked();
    }


    private void onfDateBoxActionEvent() {
        if (isForecastDataWorkRunning()) {
            return;
        }
        try {
            fLeft.updateTimeComboBox(forecastDataStorage.getAvailableTimesStringArray(TimeManager.formatToRawDate(fLeft.getDateSelectedItem())), null);
        } catch (Exception e) {
            System.out.println("GUI - fDateBoxEvent(): " +e);
        }
    }

    public void setDB(DB db) {
        this.db = db;
    }

    void loadingScreen(boolean setLoading) {
        if (setLoading) {
            loading.setBounds(WINDOW_WIDTH / 2 - 300 / 2, WINDOW_HEIGHT / 2 - MAX_BOX_HEIGHT, 300, MAX_BOX_HEIGHT);
            loading.setVisible(true);
            loading.setOpaque(false);
            loading.setBackground(BG_COLOR);
            cont.add(loading);
        } else {
            cont.remove(loading);
        }
    }

    public void getForecastResultsBoxClicked() {
        if (isForecastDataWorkRunning()) {
            return;
        }
        startingForecastDataWork();
        Thread t = new Thread() {
            public void run() {
                try {
                    String townFullName = String.valueOf(fLeft.getTownSelectedItem()); // https://stackoverflow.com/questions/4962416/preferred-way-of-getting-the-selected-item-of-a-jcombobox
                    String date = String.valueOf(fLeft.getDateSelectedItem());
                    String time = String.valueOf(fLeft.getTimeSelectedItem());

                    System.out.println("GUI - getForecastResultsBoxClicked(): " +townFullName+ " " +date+ " " +time);

                    if (townFullName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                        System.out.println("WARNING - getForecastResultsBoxClicked(): incomplete data - '" + townFullName + "', '" + date + "', '" + time +"'");
                        endingForecastDataWork();
                        return;
                    }

                    if (forecastDataStorage == null) {
                        forecastDataStorage = new ForecastDataStorage(db, townFullName, date, time);
                    } else {
                        forecastDataStorage.update(townFullName, date, time);
                    }

                    updateForecastGUI();

                    pause(250);
                    endingForecastDataWork();
                } catch (Exception e) {
                    System.out.println("Thread (" + Thread.currentThread().getId()
                            + "): Error in ComboBox displayForecast(): " + e);
                }
            };
        };
        t.start();
    }

    //3 methods below are for only letting 1 displayForecast() thread run.
    public boolean startingForecastDataWork() {
        isForecastDataWorkRunning = true;
        return isForecastDataWorkRunning;
    }

    public boolean endingForecastDataWork() {
        isForecastDataWorkRunning = false;
        return isForecastDataWorkRunning;
    }

    public boolean isForecastDataWorkRunning() {
        return isForecastDataWorkRunning;
    }

    public void incrementSelectedDateTime(DateTimeArrowBox.Direction direction, WeatherDataStorage dataStorage, ButtonPanel buttonPanel) {
        System.out.println("Advancing date time " +direction.toString()+ " by 1h...");

        String townFullName = String.valueOf(buttonPanel.getTownSelectedItem());
        String date = String.valueOf(buttonPanel.getDateSelectedItem());
        String time = String.valueOf(buttonPanel.getTimeSelectedItem());

        String[] formattedDateTime = WeatherDataStorage.formatToRawDateTimePair(date, time);
        date = formattedDateTime[0];
        time = formattedDateTime[1];

        String fwDateTime[];

        if (direction == DateTimeArrowBox.Direction.RIGHT) {
            System.out.println("Updating dateTime forward for " +townFullName+ " from " +date+ " " +time+ "...");
            fwDateTime = dataStorage.getNextValidDateTime(date, time);
        } else {
            System.out.println("Updating dateTime backward for " +townFullName+ " from " +date+ " " +time+ "...");
            fwDateTime = dataStorage.getPrevValidDateTime(date, time);
        }

        fwDateTime = TimeManager.formatRawDateTimeToFormattedArray(fwDateTime);
        date = fwDateTime[0];
        time = fwDateTime[1];
        System.out.println("...successfully updated dateTime to " +fwDateTime[0]+ " " +fwDateTime[1]);

        if (dataStorage == null) {
            dataStorage = new ForecastDataStorage(db, townFullName, date, time);
        } else {
            dataStorage.update(townFullName, date, time);
        }

        System.out.println("...successfully incremented date time " +direction.toString());
        System.out.println("Updating date & time combo boxes... ");
        if (dataStorage == forecastDataStorage) {
            updateForecastComboBoxesDateTime(date, time);
        } else {
            updateStationComboBoxesDateTime(date, time);
        }
        System.out.println("...successfully updated with " +date+ " " +time);
        System.out.println();
    }

    public void onForecastRightActionEvent() {
        if (isForecastDataWorkRunning()) return;
        startingForecastDataWork();
        incrementSelectedDateTime(DateTimeArrowBox.Direction.RIGHT, forecastDataStorage, fLeft);
        endingForecastDataWork();
        updateForecastGUI();
    }

    public void onForecastLeftActionEvent() {
        if (isForecastDataWorkRunning()) return;
        startingForecastDataWork();
        incrementSelectedDateTime(DateTimeArrowBox.Direction.LEFT, forecastDataStorage, fLeft);
        endingForecastDataWork();
        updateForecastGUI();
    }

    public void onStationRightActionEvent() {
        if (isStationDataWorkRunning()) return;
        startingStationDataWork();
        incrementSelectedDateTime(DateTimeArrowBox.Direction.RIGHT, stationDataStorage, sLeft);
        endingStationDataWork();
        updateStationGUI();
    }

    public void onStationLeftActionEvent() {
        if (isStationDataWorkRunning()) return;
        startingStationDataWork();
        incrementSelectedDateTime(DateTimeArrowBox.Direction.LEFT, stationDataStorage, sLeft);
        endingStationDataWork();
        updateStationGUI();
    }

    public void updateForecastGUI() {
        String[][] tableData = prepareHourlyDataTable(FORECAST_TABLE_FIRST_COLUMN, forecastDataStorage.getCurData());
        DefaultTableModel model;
        if (fTable != null) {
            model = (DefaultTableModel) fTable.getModel();
            model.setRowCount(0);
            for (Object[] row : tableData) {
                model.addRow(row);
            }
            fTable.setModel(model);
        } else {
            model = new DefaultTableModel(tableData, TableElement.defaultColumnNames);
            fTable = new TableElement(GUI.TABLE_WIDTH, GUI.TABLE_HEIGHT, model, "Forecast");
            fCenter.add(fTable);
        }
        fTable.updateUI();
    }

    public void updateStationGUI() {
        String[][] tableData = prepareHourlyDataTable(STATION_TABLE_FIRST_COLUMN, stationDataStorage.getCurData());
        DefaultTableModel model;
        if (sTable != null) {
            model = (DefaultTableModel) sTable.getModel();
            model.setRowCount(0);
            for (Object[] row : tableData) {
                model.addRow(row);
            }
            sTable.setModel(model);
        } else {
            model = new DefaultTableModel(tableData, TableElement.defaultColumnNames);
            sTable = new TableElement(GUI.TABLE_WIDTH, GUI.TABLE_HEIGHT, model, "Station data");
            sCenter.add(sTable);
        }
        sTable.updateUI();
    }


    public void getStationResultsBoxClicked() {
        if (isStationDataWorkRunning()) {
            return;
        }
        startingStationDataWork();
        Thread t = new Thread() {
            public void run() {
                try {
                    String townFullName = String.valueOf(sLeft.getTownSelectedItem());
                    String date = String.valueOf(sLeft.getDateSelectedItem());
                    String time = String.valueOf(sLeft.getTimeSelectedItem());

                    System.out.println("GUI - getStationResultsBoxClicked(): " +townFullName+ " " +date+ " " +time);

                    if (townFullName.isEmpty() || date.isEmpty() || time.isEmpty()) {
                        System.out.println("WARNING - getStationResultsBoxClicked(): incomplete data - '" + townFullName + "', '" + date + "', '" + time +"'");
                        endingStationDataWork();
                        return;
                    }

                    if (stationDataStorage == null) {
                        stationDataStorage = new StationDataStorage(db, townFullName, date, time);
                    } else {
                        stationDataStorage.update(townFullName, date, time);
                    }

                    updateStationGUI();

                    pause(250);
                    endingStationDataWork();
                } catch (Exception e) {
                    System.out.println("Thread (" + Thread.currentThread().getId()
                        + "): Error in GUI getStationResultsBoxClicked(): " + e);
                }
            };
        };
        t.start();
    }

    //3 methods below are for only letting 1 displayForecast() thread run.
    private boolean startingStationDataWork() {
        isStationDataWorkRunning = true;
        return isStationDataWorkRunning;
    }

    private boolean endingStationDataWork() {
        isStationDataWorkRunning = false;
        return isStationDataWorkRunning;
    }

    public boolean isStationDataWorkRunning() {
        return isStationDataWorkRunning;
    }

    public void pause(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
            System.out.println("Thread problems: " + e);
        }
    }

    private void updateStationComboBoxes() {
        try {
            sLeft.updateTimeComboBox(db.getAvailableTimesForDate(TimeManager.formatDateFromDotsToDashes(sLeft.getDateSelectedItem())), null);
        } catch (Exception e2) {
            System.out.println("GUI - updateStationComboBoxes(): " +e2);
        }
    }

    private void updateStationComboBoxesDateTime(String date, String time) {
        try {
            sLeft.updateTimeComboBox(stationDataStorage.getAvailableTimesStringArray(date), time);
            sLeft.setDateBoxSelectedItem(date);
        } catch (Exception e) {
            System.out.println("GUI - updateStationComboBoxesDateTime(): " +e);
        }
    }

    private void updateForecastComboBoxesDateTime(String date, String time) {
        try {
            fLeft.updateTimeComboBox(forecastDataStorage.getAvailableTimesStringArray(date), time);
            fLeft.setDateBoxSelectedItem(date);
        } catch (Exception e) {
            System.out.println("GUI - updateForecastComboBoxesDateTime(): " +e);
        }
    }

    public static String[][] prepareHourlyDataTable(String[] firstColumn, String[] hourlyForecast) {
        String[][] out = new String[firstColumn.length][2];
        for (int i = 0; i < firstColumn.length; i++) {
            out[i][0] = firstColumn[i];
            out[i][1] = hourlyForecast[i];
        }
        return out;
    }
}
