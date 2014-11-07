package databaseexercise;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

public class JMetropolisViewer extends JFrame {
	
	public static final String APP_TITLE = "Metropolis Viewer";
	public static final String METROPOLIS = "Metropolis";
	public static final String POPULATION = "Population";
	public static final String CONTINENT = "Continent";
	
	private JTextField cityNameInput;
	private JTextField cityContinentInput;
	private JTextField cityPopulationInput;
	
	private JButton addButton;
	private JButton searchButton;
	
	private JComboBox<String> populationSearchOptions;
	private JComboBox<String> matchSearchOptions;
	
	public JMetropolisViewer() {
		super(APP_TITLE);
		setLayout(new BorderLayout(4, 4));
		add(makeInputFieldsPanel(), BorderLayout.NORTH);
		add(makeResultTable(), BorderLayout.CENTER);
		
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
	
	private JScrollPane makeResultTable() {
		JTable resultTable = new JTable(new MetropolisTableModel());
		JScrollPane scrollPane = new JScrollPane(resultTable);
		
		return scrollPane;
	}
}
