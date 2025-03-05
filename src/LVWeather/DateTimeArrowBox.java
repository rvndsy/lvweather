package LVWeather;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class DateTimeArrowBox extends JButton {
    private static final long serialVersionUID = 1L; // Auto-generated thing

    public enum Direction {
        LEFT,
        RIGHT,
    };
    private Direction direction;

    private static String leftText = "<";
    private static String rightText = ">";

    public DateTimeArrowBox() {}

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (direction == Direction.LEFT) {
            this.setText(leftText);
        }
        if (direction == Direction.RIGHT) {
            this.setText(rightText);
        }
    }

    private void updateDirection(Direction direction) {
        this.direction = direction;
        if (direction == Direction.LEFT) {
            this.setText(leftText);
        }
        if (direction == Direction.RIGHT) {
            this.setText(rightText);
        }
    }
}
