package com.bk.theweatherlive;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<ForecastData> {
	private final Context context;
	private final ArrayList<ForecastData> data;
	
	MyArrayAdapter(Context context, ArrayList<ForecastData> data) {
		super(context, R.layout.fragment_list_view, data);
		this.context = context;
		this.data = data;
		Log.d("twl", "Yay, passed constructor");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ForecastData element = data.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listFragment = inflater.inflate(R.layout.fragment_list_view, parent, false);
		((TextView) listFragment.findViewById(R.id.listItemDate)).setText(element.getTime());
		((TextView) listFragment.findViewById(R.id.listItemTemp)).setText(element.getTemp());
		((TextView) listFragment.findViewById(R.id.listItemCondition)).setText(element.getWeatherString());
		return listFragment;
	}
}
