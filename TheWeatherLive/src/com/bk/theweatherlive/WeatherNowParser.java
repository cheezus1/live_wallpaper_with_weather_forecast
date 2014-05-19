package com.bk.theweatherlive;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class WeatherNowParser {
	
	public String parse(InputStream in) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			return readFeed(parser);
		} finally {
			in.close();
		}
	}
	
	private String readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		String weatherCode = "";
		String cityName = "";
		String temp = "";
		String maxTemp = "";
		String minTemp = "";
		String humidity = "";
		String pressure = "";
		String wind = "";
		String weatherString = "";
		
		Boolean celsius = false;
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			if(name.equals("city")) {
				cityName = parser.getAttributeValue(1) + " \n";
			} else if(name.equals("temperature")) {
				celsius = parser.getAttributeValue(3).equals("celsius");
				temp = parser.getAttributeValue(0);
				if(celsius) {
					temp += "°C \n";
					maxTemp = parser.getAttributeValue(2) + "°C \n";
					minTemp = parser.getAttributeValue(1) + "°C \n";
				} else {
					temp += "°F \n";
					maxTemp = parser.getAttributeValue(2) + "°F \n";
					minTemp = parser.getAttributeValue(1) + "°F \n";
				}
			} else if(name.equals("humidity")) {
				humidity = parser.getAttributeValue(0) + parser.getAttributeValue(1) + " \n";
			} else if(name.equals("pressure")) {
				pressure = parser.getAttributeValue(0) + parser.getAttributeValue(1) + " \n";
			} else if(name.equals("weather")) {
				weatherCode = parser.getAttributeValue(0) + "\n";
				weatherString = parser.getAttributeValue(1).toUpperCase(Locale.getDefault()) + " \n";
			} else if(name.equals("speed")) {
				if(celsius) {
					wind = parser.getAttributeValue(0) + " km/h \n";
				} else {
					wind = parser.getAttributeValue(0) + " mph \n";
				}
			}
		}
		return weatherCode + cityName +  temp + maxTemp + minTemp + humidity + pressure + wind + weatherString;
	}
}
