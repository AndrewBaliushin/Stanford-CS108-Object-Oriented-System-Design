package databaseexercise;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database {
	
	private static final String METROPOLIS_FIELD = MyDBInfo.METROPOLIS_FIELD;
	private static final String CONTINENT_FIELD = MyDBInfo.CONTINENT_FIELD;
	private static final String POPULATION_FIELD = MyDBInfo.POPULATION_FIELD;

	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	private List<City> citiesList;
	
	public Database(List<City> citiesList) {
		this.citiesList = citiesList;
	}

	private void readDataBase(String SQL) throws SQLException {
	    try {
	      connect();
	      statement = connection.createStatement();
	      resultSet = statement
	          .executeQuery(SQL);
	      
	      citiesList.clear();
	      
	      while (resultSet.next())
	      {
	    	 citiesList.add(new City(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3)));
	      } 
	      
	    } catch (Exception e) {
	    	System.out.println(e);
	    	System.err.println("SQL: " + SQL);
	    } finally {
	      connection.close();
	    }

	  }
	
	public void readAll() {
		try {
			readDataBase("SELECT * FROM " + MyDBInfo.TABLE_NAME);
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void search(String city, String continent, int population, boolean partialMatch,
			boolean popLargerThan) {
		if (city.length() == 0 && continent.length() ==0 && population == 0) {
			readAll();
			return;
		}
		
		StringBuilder sbSql = new StringBuilder();
		
		sbSql.append("SELECT * FROM ");
		sbSql.append(MyDBInfo.TABLE_NAME);
		sbSql.append(" WHERE ");
		
		List<String> whereStatments = new ArrayList<String>();
		
		if (city.length() > 0 && partialMatch) {
			whereStatments.add(METROPOLIS_FIELD + " LIKE '%" + city + "%'");
		} else if (city.length() > 0) {
			whereStatments.add(METROPOLIS_FIELD + " = '" + city + "'");
		}
		
		if (continent.length() > 0 && partialMatch) {
			whereStatments.add(CONTINENT_FIELD + " LIKE '%" + continent + "%'");
		} else if (continent.length() > 0) {
			whereStatments.add(CONTINENT_FIELD + " = '" + continent + "'");
		}
		
		if (population != 0 && popLargerThan) {
			whereStatments.add(POPULATION_FIELD + " > " + population);
		} else if (population != 0) {
			whereStatments.add(POPULATION_FIELD + " < " + population);
		}
		
		Iterator<String> iter = whereStatments.iterator();
		boolean first = true;
		while (iter.hasNext()) {
			if (!first) {
				sbSql.append(" AND ");
			} else {
				first = false;
			}
			sbSql.append(iter.next());			
		}
		
		
		try {
			readDataBase(sbSql.toString());
		} catch (SQLException e) {
			System.out.println("SQL: " + sbSql.toString());
			e.printStackTrace();
		}
	}
	
	public void add(City city) throws SQLException {
		String sql = "INSERT INTO " + MyDBInfo.TABLE_NAME
				+ " (" + METROPOLIS_FIELD +" , " + CONTINENT_FIELD + ", "
				+ POPULATION_FIELD + ") " + "VALUES (?, ? , ?)";
		
		if (city == null) {
			System.out.println("City is null");
			return;
		}

		try {
			connect();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, city.getCityName());
			preparedStatement.setString(2, city.getCityContinent());
			preparedStatement.setInt(3, city.getPopulation());
			preparedStatement.executeUpdate();
			citiesList.add(city);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		} finally {
			connection.close();
			preparedStatement.close();
		}

	}

	private void connect() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
	    connection = DriverManager.
	    		  getConnection(MyDBInfo.MYSQL_DATABASE_SERVER + MyDBInfo.MYSQL_DATABASE_NAME,
	    		  MyDBInfo.MYSQL_USERNAME, MyDBInfo.MYSQL_PASSWORD);
	}
}
