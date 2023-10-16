package LVWeather;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTable;

// Table for displaying selected weather/forecast data		https://docs.oracle.com/javase/8/docs/api/javax/swing/JTable.html
public class TableElement extends JTable{
	private static final long serialVersionUID = 1L; // Auto-generated thing

	String tableName;
	static String[] defaultColumnNames = { "Type of data", "Actual data" };
	static String[][] defaultData = {{"Table is empty", "No data"}};
	static ArrayList<TableElement> allTableElements = new ArrayList<TableElement>();

	TableElement(int x, int y, int width, int height, String[][] tableData, String tableName) {
		super(tableData, defaultColumnNames);
		setBounds(x, y, width, height);
		setOpaque(false);
		setBackground(Main.bgColor);
		setShowVerticalLines(false);
        setVisible(true);
        setEditingColumn(0);
        setFont(new Font(Main.FONT_NAME, Font.BOLD, 16));
        setRowHeight(this.getRowHeight()+20);
    }
	
	/*TableElement(int x, int y, int width, int height){
		super(defaultData, defaultColumnNames);
		setBounds(x, y, width, height);
		setOpaque(false);
		setBackground(Main.bgColor);
		setShowVerticalLines(false);
        setVisible(true);
        setEditingColumn(0);
        //this.getColumnModel().getColumn(0).setMinWidth(width / 2);
    }*/
	
	// disables text editing within table		https://stackoverflow.com/questions/9919230/disable-user-edit-in-jtable
	public boolean isCellEditable(int row, int column){ 
        return false;  
    }
}
