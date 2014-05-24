package com.bk.theweatherlive;

public class ForecastData extends WeatherData {
	String time;

	public ForecastData() {
		super();
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
