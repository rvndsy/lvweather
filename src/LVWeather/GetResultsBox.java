package LVWeather;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class GetResultsBox extends JButton implements ActionListener{
	private static final long serialVersionUID = 1L; // Auto-generated thing
	
	static String text = "Get result";
	boolean isForecast;
	
	public GetResultsBox(int x, int y, int width, int height) {
		setText(text);
		setBounds(x, y, width, height);
		setVisible(true);
		setOpaque(true);
		addActionListener(this);
	}
	
	public void linkToForecast() {
		this.isForecast = true;
	}
	
	public void linkToStation() {
		this.isForecast = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.isForecast) {			
			ForecastComboBox.getResultsBoxClicked();
		} else {
			StationComboBox.getResultsBoxClicked();
		}
	}
}