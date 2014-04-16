package com.bk.theweatherlive;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
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
		StringBuilder stringBuilder = new StringBuilder();
		Boolean celsius = false;
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			Log.d("TWL", "STARTED PARSING");
			
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			if(name.equals("city")) {
				Log.d("TWL", "ENTERED CASE CITY");
				stringBuilder.append(parser.getAttributeValue(1) + " \n");
			} else if(name.equals("temperature")) {
				Log.d("TWL", "ENTERED CASE TEMP");
				celsius = parser.getAttributeValue(3).equals("celsius");
				stringBuilder.append(parser.getAttributeValue(0));
				if(celsius) {
					stringBuilder.append("°C \n" + "High: " + parser.getAttributeValue(2) + "°C Low: " + parser.getAttributeValue(1) + "°C \n");
				} else {
					stringBuilder.append("°F \n" + "High: " + parser.getAttributeValue(2) + "°F Low: " + parser.getAttributeValue(1) + "°F \n");
				}
			} else if(name.equals("humidity")) {
				Log.d("TWL", "ENTERED CASE HUMIDITY");
				stringBuilder.append("Humidity: " + parser.getAttributeValue(0) + parser.getAttributeValue(1) + " \n");
			} else if(name.equals("pressure")) {
				Log.d("TWL", "ENTERED CASE PRESSURE");
				stringBuilder.append("Pressure: " + parser.getAttributeValue(0) + parser.getAttributeValue(1) + " \n");
			} else if(name.equals("weather")) {
				Log.d("TWL", "ENTERED CASE WEATHER");
				stringBuilder.append(parser.getAttributeValue(1).toUpperCase(Locale.getDefault()) + " \n");
			} else if(name.equals("speed")) {
				Log.d("TWL", "ENTERED CASE WIND SPEED");
				if(celsius) {
					stringBuilder.append("Wind: "  + parser.getAttributeValue(0) + " km/h, ");
				} else {
					stringBuilder.append("Wind: "  + parser.getAttributeValue(0) + " mph, ");
				}
			} else if(name.equals("direction")) {
				Log.d("TWL", "ENTERED CASE WIND DIRECTION");
				stringBuilder.append(parser.getAttributeValue(2) + " \n");
			}
		}
		Log.d("TWL", stringBuilder.toString());
		return stringBuilder.toString();
	}
}
