package databaseexercise;

import java.sql.*;
import java.util.List;

public class Database {

	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	private List<City> citiesList;
	
	public Database(List<City> citiesList) {
		this.citiesList = citiesList;
	}

	public void readDataBase(String SQL) throws SQLException {
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
	
	public void add(City city) throws SQLException {
		String sql = "INSERT INTO " + MyDBInfo.TABLE_NAME
				+ " (metropolis ,continent,population) " + "VALUES (?, ? , ?)";
		
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
