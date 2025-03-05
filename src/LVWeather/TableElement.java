package LVWeather;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Table for displaying selected weather/forecast data        https://docs.oracle.com/javase/8/docs/api/javax/swing/JTable.html
public class TableElement extends JTable {
    private static final long serialVersionUID = 1L; // Auto-generated thing

    static String[] defaultColumnNames = { "Type of data", "Actual data" };
    static String[][] defaultData = {{"Table is empty", "No data"}};

    private String tableName;
    private DefaultTableModel dataModel;

    TableElement(int width, int height, DefaultTableModel dataModel, String tableName) {
        super(dataModel);
        this.tableName = tableName;
        setPreferredSize(new Dimension(width, height));
        setShowVerticalLines(false);
        setEditingColumn(0);
        setOpaque(false);
        ((DefaultTableCellRenderer)this.getDefaultRenderer(Object.class)).setOpaque(false); // required to be transparent
        setFont(new Font(GUI.FONT_NAME, Font.BOLD, GUI.TABLE_FONT_SIZE));
        setRowHeight(GUI.MAX_BOX_HEIGHT - 5);
        setVisible(true);
    }

    // disables text editing within table        https://stackoverflow.com/questions/9919230/disable-user-edit-in-jtable
    @Override
    public boolean isCellEditable(int row, int column){ 
        return false;
    }
}
