package LVWeather;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class GetResultsBox extends JButton {
    private static final long serialVersionUID = 1L; // Auto-generated thing

    static String text = "Get result";

    private GUI gui;

    public GetResultsBox() {
        this.setText(text);
    }
}
