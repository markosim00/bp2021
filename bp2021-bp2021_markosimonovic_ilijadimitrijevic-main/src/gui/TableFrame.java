package gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TableFrame extends JFrame{
	
	
	public TableFrame(JTable table) {
		add(new JScrollPane(table));
		pack();
		this.setVisible(true);
	}

}
