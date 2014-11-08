package databaseexercise;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

public class JMetropolisViewer extends JFrame {
	
	public static final String APP_TITLE = "Metropolis Viewer";
	public static final String METROPOLIS = "Metropolis";
	public static final String POPULATION = "Population";
	public static final String CONTINENT = "Continent";
	
	private List<City> metropolisList = new ArrayList<City>();
	private Database db;
	private MetropolisTableModel tableModel;
	
	private JTextField cityNameInput;
	private JTextField cityContinentInput;
	private JTextField cityPopulationInput;
	
	private JButton addButton;
	private JButton searchButton;
	
	private JComboBox<String> populationSearchOptions;
	private JComboBox<String> matchSearchOptions;
	
	public JMetropolisViewer() {
		super(APP_TITLE);
		
		db = new Database(metropolisList);
		db.readAll();
		
		setLayout(new BorderLayout(4, 4));
		add(makeInputFieldsPanel(), BorderLayout.NORTH);
		tableModel = new MetropolisTableModel(metropolisList);
		add(makeResultTable(tableModel), BorderLayout.CENTER);
		
		addListners();
		
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JMetropolisViewer metropolisViewer = new JMetropolisViewer();	
		
	}
	
	private JPanel makeInputFieldsPanel() {
		JPanel inputFieldsPanel = new JPanel();
		inputFieldsPanel.setLayout(new FlowLayout());
		
		JLabel cityL = new JLabel(METROPOLIS + ":");
	    inputFieldsPanel.add(cityL);
	    cityNameInput = new JTextField(10);
	    inputFieldsPanel.add(cityNameInput);
	    
	    JLabel contL = new JLabel(CONTINENT + ":");
	    inputFieldsPanel.add(contL);
	    cityContinentInput = new JTextField(10);
	    inputFieldsPanel.add(cityContinentInput);
	    
	    JLabel populationL = new JLabel(POPULATION + ":");
	    inputFieldsPanel.add(populationL);
	    cityPopulationInput = new JTextField(10);
	    inputFieldsPanel.add(cityPopulationInput);
	    
	    inputFieldsPanel.add(makeControlPanel());
	    
	    return inputFieldsPanel;
	}
	
	private JPanel makeControlPanel() {
		JPanel contolPanel = new JPanel();
		contolPanel.setLayout(new BoxLayout(contolPanel, BoxLayout.X_AXIS));
		
		addButton = new JButton("Add");
		contolPanel.add(addButton);
		
		searchButton = new JButton("Search");
		contolPanel.add(searchButton);
		
		contolPanel.add(makeSearchOptionBox());
		
		return contolPanel;
	}
	
	private JPanel makeSearchOptionBox() {
		String[] optionsForPopulation = {"Population larger than", "Population less than"};
		String[] optionsForMatch = {"Partial match", "Exact match"};
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		optionsPanel.setBorder(new TitledBorder("Search options"));
		
		populationSearchOptions = new JComboBox<String>(optionsForPopulation);
		populationSearchOptions.setSelectedIndex(0);
		optionsPanel.add(populationSearchOptions);
		

		matchSearchOptions = new JComboBox<String>(optionsForMatch);
		matchSearchOptions.setSelectedIndex(0);
		optionsPanel.add(matchSearchOptions);
		
		return optionsPanel;		
	}
	
	private JScrollPane makeResultTable(AbstractTableModel model) {
		JTable resultTable = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(resultTable);
		
		return scrollPane;
	}
	
	private void addListners() {
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					db.add(createCity());
					tableModel.fireTableDataChanged();
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			}
		});
	}

	private City createCity() {
		String city = cityNameInput.getText();
		String continent = cityContinentInput.getText();
		int population = Integer.parseInt(cityPopulationInput.getText());
		if (city.length() != 0 && continent.length() != 0) {
			return new City(city,continent, population);
		} else {
			return null;
		}
	}
}
