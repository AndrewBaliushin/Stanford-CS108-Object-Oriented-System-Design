package databaseexercise;

import javax.swing.table.AbstractTableModel;

public class MetropolisTableModel extends AbstractTableModel {
	
	MetropolisTableModel() {
		
	}
	
	private String[] colNames = {JMetropolisViewer.METROPOLIS,
			JMetropolisViewer.CONTINENT, JMetropolisViewer.POPULATION};
	

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}


}
