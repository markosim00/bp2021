package gui.table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ResultSetTable extends JTable{
	
	private DefaultTableModel dataModel;
	
	 public ResultSetTable(ResultSet rs) throws SQLException{
		 
		    
		    dataModel = new DefaultTableModel();
		    setModel(dataModel);
		 
		    try {
		      ResultSetMetaData mdata = rs.getMetaData();
		      int colCount = mdata.getColumnCount();
		      String[] colNames = new String[colCount];
		      for (int i = 1; i <= colCount; i++) {
		        colNames[i - 1] = mdata.getColumnName(i);
		      }
		      dataModel.setColumnIdentifiers(colNames);
		 
		      while (rs.next()) {
		        String[] rowData = new String[colCount];
		        for (int i = 1; i <= colCount; i++) {
		          rowData[i - 1] = rs.getString(i);
		        }
		        dataModel.addRow(rowData);
		      }
		    }
		    finally{
		      try {
		        rs.close();
		      }
		      catch (SQLException ignore) {
		      }
		    }
		    this.setVisible(true);
		}

}
