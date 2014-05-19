package com.bk.theweatherlive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class HourlyParser {
	
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
		String weatherCode = "";
		String time = "";
		String temp = "";
		String maxTemp = "";
		String minTemp = "";
		String weatherString = "";
		
		Boolean celsius = false;
		int count = 0;
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			
			if(parser.getEventType() == XmlPullParser.END_TAG) {
				count++;
			}
			
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			if(name.equals("time")) {
				time = parser.getAttributeValue(0).replace("T", " ") + " \n";
			} else if(name.equals("symbol")) {
				weatherCode = parser.getAttributeValue(0) + "\n";
				weatherString = parser.getAttributeValue(1).toUpperCase(Locale.getDefault()) + " \n";
			} else if(name.equals("temperature")) {
				celsius = parser.getAttributeValue(0).equals("celsius");
				temp = parser.getAttributeValue(1);
				if(celsius) {
					temp += "°C \n";
					maxTemp = parser.getAttributeValue(3) + "°C \n";
					minTemp = parser.getAttributeValue(2) + "°C \n";
				} else {
					temp += "°F \n";
					maxTemp = parser.getAttributeValue(3) + "°F \n";
					minTemp = parser.getAttributeValue(2) + "°F \n";
				}
				stringBuilder.append(weatherCode + time + temp + maxTemp + minTemp + weatherString);
			}
			if(count > 81){
				break;
			}
		}
		return stringBuilder.toString();
	}
}
