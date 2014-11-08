package databaseexercise;

public class City {
	private String cityName;
	private String cityContinent;
	private int population;
	
	public City(String cityName, String cityContinent, int population) {
		this.cityName = cityName;
		this.cityContinent = cityContinent;
		this.population = population;
	}
	
	public String getCityName() {
		return cityName;
	}
	public String getCityContinent() {
		return cityContinent;
	}
	public int getPopulation() {
		return population;
	}
	
}
