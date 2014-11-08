package databaseexercise;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class MetropolisTableModel extends AbstractTableModel {
	
	private List<City> cityList;
	
	MetropolisTableModel(List<City> cityList) {
		this.cityList = cityList;
	}
	
	private String[] colNames = {JMetropolisViewer.METROPOLIS,
			JMetropolisViewer.CONTINENT, JMetropolisViewer.POPULATION};
	
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}

	@Override
	public int getColumnCount() {
		return colNames.length;
	}
	
	@Override
	public int getRowCount() {
		return cityList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(row < 0 || row >= cityList.size()) return null;
        City obj = cityList.get(row);
        switch(column)
        {
            case 0: return obj.getCityName();
            case 1: return obj.getCityContinent();
            case 2: return obj.getPopulation();
            default: return null;
        }
	}


}
