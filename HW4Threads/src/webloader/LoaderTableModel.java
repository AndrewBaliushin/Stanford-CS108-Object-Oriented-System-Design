package webloader;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import webloader.WebFrame.URLtoDownload;

public class LoaderTableModel extends AbstractTableModel{
	
	private List<URLtoDownload> URLs;

	LoaderTableModel(List<URLtoDownload> URLs) {
		this.URLs = URLs;
	}
	
	private String[] colNames = { "url", "status"};
	
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
		return URLs.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(row < 0 || row >= URLs.size()) return null;
        URLtoDownload obj = URLs.get(row);
        switch(column)
        {
            case 0: return obj.getAdress();
            case 1: return obj.getStatusMessage();
            default: return null;
        }
	}
}
