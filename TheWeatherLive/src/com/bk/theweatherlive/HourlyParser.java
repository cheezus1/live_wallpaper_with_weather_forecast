package com.bk.theweatherlive;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
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
		Boolean celsius = false;
		int count = 0;
		
		while(parser.next() != XmlPullParser.END_DOCUMENT) {
			Log.d("TWL", "STARTED PARSING2");
			
			if(parser.getEventType() == XmlPullParser.END_TAG) {
				count++;
			}
			
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			Log.d("TWL", "GOT NAME");
			if(name.equals("time")) {
				Log.d("TWL", "ENTERED CASE TIME");
				stringBuilder.append("\n" + parser.getAttributeValue(0).replace("T", " ") + " \n");
			} else if(name.equals("temperature")) {
				Log.d("TWL", "ENTERED CASE TEMP");
				celsius = parser.getAttributeValue(0).equals("celsius");
				stringBuilder.append(parser.getAttributeValue(1));
				if(celsius) {
					stringBuilder.append("°C \n" + "High: " + parser.getAttributeValue(3) + "°C Low: " + parser.getAttributeValue(2) + "°C \n");
				} else {
					stringBuilder.append("°F \n" + "High: " + parser.getAttributeValue(3) + "°F Low: " + parser.getAttributeValue(2) + "°F \n");
				}
			} else if(name.equals("clouds")) {
				Log.d("TWL", "ENTERED CASE CLOUDS");
				stringBuilder.append(parser.getAttributeValue(0) + "\n");
			}
			if(count > 81){
				break;
			}
		}
		Log.d("TWL", "FINISHED PARSING2");
		Log.d("TWL", stringBuilder.toString());
		return stringBuilder.toString();
	}
}
