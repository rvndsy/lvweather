package LVWeather;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class ButtonPanel extends JPanel {
    private JComboBox<String> townBox;
    private JComboBox<String> dateBox;
    private JComboBox<String> timeBox;
    private GetResultsBox getResultsBox;
    private DateTimeArrowBox dateTimeArrowLeft;
    private DateTimeArrowBox dateTimeArrowRight;

    private DB db;

    ButtonPanel() {
        townBox = createComboBox(GUI.LEFT_PANE_LARGE_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT);
        dateBox = createComboBox(GUI.LEFT_PANE_SMALL_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT);
        timeBox = createComboBox(GUI.LEFT_PANE_SMALL_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT);

        getResultsBox = createGetResultsBox(GUI.LEFT_PANE_LARGE_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT);

        //getForecastBox.linkToForecast();

        dateTimeArrowLeft = createDateTimeArrowBox(GUI.LEFT_PANE_SMALL_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT, DateTimeArrowBox.Direction.LEFT);
        dateTimeArrowRight = createDateTimeArrowBox(GUI.LEFT_PANE_SMALL_BUTTON_WIDTH, GUI.LEFT_PANE_BUTTON_HEIGHT, DateTimeArrowBox.Direction.RIGHT);

        this.setPreferredSize(new Dimension(GUI.LEFT_PANE_WIDTH, GUI.WINDOW_HEIGHT - 50));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.setBorder(new MatteBorder(0, 0, 0, 1, Color.GRAY));

        this.add(townBox);
        this.add(dateBox);
        this.add(timeBox);
        this.add(getResultsBox);
        this.add(dateTimeArrowLeft);
        this.add(dateTimeArrowRight);
    }

    private JComboBox<String> createComboBox(int width, int height) {
        JComboBox<String> newComboBox = new JComboBox<String>();
        newComboBox.setPreferredSize(new Dimension(width, height));
        newComboBox.setVisible(true);
        newComboBox.setFont(GUI.COMBOBOX_FONT);
        newComboBox.setEditable(true);
        newComboBox.updateUI();

        AutoCompleteDecorator.decorate(newComboBox);

        return newComboBox;
    }

    private GetResultsBox createGetResultsBox(int width, int height) {
        GetResultsBox newResBox = new GetResultsBox();
        newResBox.setPreferredSize(new Dimension(width, height));
        newResBox.setVisible(true);
        newResBox.setOpaque(true);
        return newResBox;
    }

    private DateTimeArrowBox createDateTimeArrowBox(int width, int height, DateTimeArrowBox.Direction direction) {
        DateTimeArrowBox newTimeArrowBox = new DateTimeArrowBox();
        newTimeArrowBox.setDirection(direction);
        newTimeArrowBox.setPreferredSize(new Dimension(width, height));
        newTimeArrowBox.setVisible(true);
        newTimeArrowBox.setOpaque(true);
        return newTimeArrowBox;
    }

    private void updateComboBoxList(JComboBox<String> comboBox, String[] list, String selectedItem) {
        DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(list);
        if (selectedItem != null) {
            newModel.setSelectedItem(selectedItem);
        }
        comboBox.setModel(newModel);
    }

    public void updateTownComboBox(String[] list, String selectedItem) {
        updateComboBoxList(this.townBox, list, selectedItem);
    }

    public void updateDateComboBox(String[] list, String selectedItem) {
        updateComboBoxList(this.dateBox, list, selectedItem);
    }

    public void updateTimeComboBox(String[] list, String selectedItem) {
        updateComboBoxList(this.timeBox, list, selectedItem);
    }

    public void addTownBoxEventListener(Runnable action) {
        townBox.addActionListener(e -> {
            action.run();
        });
    }

    public void addTimeBoxEventListener(Runnable action) {
        timeBox.addActionListener(e -> {
            action.run();
        });
    }

    public void addDateBoxEventListener(Runnable action) {
        dateBox.addActionListener(e -> {
            action.run();
        });
    }

    public void addGetResultsEventListener(Runnable action) {
        getResultsBox.addActionListener(e -> {
            action.run();
        });
    }

    public void addTimeLeftEventListener(Runnable action) {
        dateTimeArrowLeft.addActionListener(e -> {
            action.run();
        });
    }

    public void addTimeRightEventListener(Runnable action) {
        dateTimeArrowRight.addActionListener(e -> {
            action.run();
        });
    }

    public String getDateSelectedItem() {
        return dateBox.getSelectedItem().toString();
    }

    public String getTimeSelectedItem() {
        return timeBox.getSelectedItem().toString();
    }

    public String getTownSelectedItem() {
        return townBox.getSelectedItem().toString();
    }

    public void setDateBoxSelectedItem(String date) {
        dateBox.setSelectedItem(date);
    }
}
