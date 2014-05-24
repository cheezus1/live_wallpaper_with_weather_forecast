package com.bk.theweatherlive;

public abstract class WeatherData {
	int weatherCode;
	String temp;
	String maxTemp;
	String minTemp;
	String weatherString;
	public int getWeatherCode() {
		return weatherCode;
	}
	public void setWeatherCode(int weatherCode) {
		this.weatherCode = weatherCode;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
	}
	public String getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(String minTemp) {
		this.minTemp = minTemp;
	}
	public String getWeatherString() {
		return weatherString;
	}
	public void setWeatherString(String weatherString) {
		this.weatherString = weatherString;
	}
	
	
}
