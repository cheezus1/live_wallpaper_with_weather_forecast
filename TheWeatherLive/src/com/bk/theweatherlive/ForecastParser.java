package com.bk.theweatherlive;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class ForecastParser {
private static final String ns = null;
	
	public String parse(InputStream in, String units) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			return readFeed(parser, units);
		} finally {
			in.close();
		}
	}
	
	private String readFeed(XmlPullParser parser, String units) throws XmlPullParserException, IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Boolean celsius = units.equals("metric");
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			Log.d("TWL", "STARTED PARSING3");
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			Log.d("TWL", "GOT NAME");
			if(name.equals("time")) {
				Log.d("TWL", "ENTERED CASE TIME");
				stringBuilder.append("\n" + parser.getAttributeValue(0) + " \n");
			} else if(name.equals("temperature")) {
				Log.d("TWL", "ENTERED CASE TEMP");
				stringBuilder.append(parser.getAttributeValue(0));
				if(celsius) {
					stringBuilder.append("°C \n" + "High: " + parser.getAttributeValue(2) + "°C Low: " + parser.getAttributeValue(1) + "°C \n");
				} else {
					stringBuilder.append("°F \n" + "High: " + parser.getAttributeValue(2) + "°F Low: " + parser.getAttributeValue(1) + "°F \n");
				}
			} else if(name.equals("clouds")) {
				Log.d("TWL", "ENTERED CASE CLOUDS");
				stringBuilder.append(parser.getAttributeValue(0) + "\n");
			}
		}
		Log.d("TWL", "FINISHED PARSING2");
		Log.d("TWL", stringBuilder.toString());
		return stringBuilder.toString();
	}
}
