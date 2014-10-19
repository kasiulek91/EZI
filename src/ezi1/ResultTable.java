package ezi1;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class ResultTable extends JFrame {

	private JTable table;
	private JTextArea txt = new JTextArea(4, 20);

	public ResultTable() {
		init();
	}

	class DataModel extends AbstractTableModel {
		Object[][] data = new Object[0][2];
		String[] columnNames = { "Tytul", "Score" };

		// Prints data when table changes:
		class TML implements TableModelListener {
			public void tableChanged(TableModelEvent e) {
				txt.setText(""); // Clear it
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[0].length; j++)
						txt.append(data[i][j] + " ");
					txt.append("\n");
				}
			}
		}

		public String getColumnName(int col) {
			return columnNames[col].toString();
		}

		public DataModel() {
			addTableModelListener(new TML());
		}

		public int getColumnCount() {
			return 2;
		}
		
		public void removeRows() {
			int prevSize = data.length;
			data = new Object[0][2];
			fireTableRowsDeleted(0, prevSize);
		}


		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public void setValueAt(Object val, int row, int col) {

			data[row][col] = val;
			// Indicate the change has happened:
			fireTableDataChanged();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void addRows(int size) {
			int prevSize = data.length;
			data = new Object[size][2];
			fireTableRowsInserted(prevSize, size);
		}
	}

	public void init() {
		Container cp = getContentPane();
		table = new JTable(new DataModel());
		table.setShowGrid(false);
		cp.add(new JScrollPane(table));
		cp.add(BorderLayout.SOUTH, txt);
		table.getColumn("Score").setMaxWidth(75);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
	}

	public JScrollPane getScrollPane() {
		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
	}
	
	public void clearTable() {
		DataModel model = (DataModel) table.getModel();
		model.removeRows();
	}

	public void addNewValues(Vector<DocScore> scores) {
		DataModel model = (DataModel) table.getModel();
		model.addRows(scores.size());
		for (int i = 0; i < scores.size(); i++) {
			table.setValueAt(scores.get(i).getDoc(), i, 0);
			table.setValueAt(scores.get(i).getScore(), i, 1);
		}
	}

}
