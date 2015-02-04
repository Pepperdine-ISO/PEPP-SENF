/*
 *
 * Senf was created by the Information Security Office
 * at the Univeristy of Texas at Austin.
 * 
 * This work is licensed under the Creative Commons 
 * Attribution-NonCommercial-ShareAlike 3.0 United States
 * License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 *
 * Send comments to security@utexas.edu
 *
 */ 
 
package senf;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

class SenfResultsTableModel extends DefaultTableModel
{
	SenfResultsTableModel(Object[][] data, String[] columnNames)
	{
		super(data, columnNames);
	}

	Class[] types = new Class[] { String.class };

	boolean[] canEdit = new boolean[] { false };

	public Class getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit[columnIndex];
	}

	public void addResult(SenfResult result)
	{
		Vector v = new Vector();
		v.add(result);
		dataVector.add(v);
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}

	public void clearTable()
	{
		int count = Math.max(0, dataVector.size() - 1);
		dataVector.clear();
		fireTableRowsDeleted(0, count);
	}
}
