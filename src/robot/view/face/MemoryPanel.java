package robot.view.face;

import robot.media.Memory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 21.09.11
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
public class MemoryPanel extends JPanel {
    protected JTable memoryTable;

    public void setData(HashMap<String, Memory.MemoryItem> memoryItems) {
        MyTableModel tableModel = new MyTableModel();
        tableModel.setData(memoryItems);
        memoryTable.setModel(tableModel);
    }

    public MemoryPanel() {
        memoryTable = new JTable(new MyTableModel());
        memoryTable.setColumnSelectionAllowed(false);
        memoryTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        memoryTable.getColumnModel().getColumn(0).setWidth(200);
        memoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        memoryTable.getColumnModel().getColumn(1).setWidth(200);

        add(memoryTable);
    }

    private class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Key", "Value"};
        private String[][] data = new String[1][2];
        {
            data[0][0] = "NA";
            data[0][1] = "NA";
        }

        public void setData(HashMap<String, Memory.MemoryItem> memoryData){
            if (memoryData.size() == 0) return;
            data = new String[memoryData.size()][2];
            Set <String> memoryKeys = memoryData.keySet();
            int index = 0;
            for(String key : memoryKeys){
                data[index][0] = memoryData.get(key).getValue().toString();
                data[index][1] = key;
                index++;
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
                return false;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(String value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

}
