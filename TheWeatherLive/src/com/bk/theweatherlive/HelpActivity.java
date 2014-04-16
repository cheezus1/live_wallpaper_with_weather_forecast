package com.bk.theweatherlive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HelpActivity extends Activity {

	ListView helpList;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		displayList();
	}
 
	public void displayList() {
		helpList = (ListView) findViewById(R.id.helpList);
  
		String[] listItems = {"General",
							"Hourly section",
							"Now section",
							"Next days section",
							"Refreshing weather data",
							"Action bar",
							"Changing settings of the application",
							"Send feedback"};
  
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
												android.R.layout.simple_list_item_1, 
												android.R.id.text1, 
												listItems);
  
		helpList.setAdapter(adapter);
  
		helpList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
    
				int itemPosition = position;
    
				//String itemValue = (String) helpList.getItemAtPosition(position);
    
				switch(itemPosition) {
					case 0:
						Intent generalHelpIntent = new Intent(getApplicationContext(), GeneralHelp.class);
						startActivity(generalHelpIntent, null);
						break;
					case 1:
						Intent hourlyHelpIntent = new Intent(getApplicationContext(), HourlySectionHelp.class);
						startActivity(hourlyHelpIntent, null);
						break;
					case 2:
						Intent nowSectionHelpIntent = new Intent(getApplicationContext(), NowSectionHelp.class);
						startActivity(nowSectionHelpIntent, null);
						break;
					case 3:
						Intent nextDaysHelpIntent = new Intent(getApplicationContext(), NextDaysSectionHelp.class);
						startActivity(nextDaysHelpIntent, null);
						break;
					case 4:
						Intent refreshingWeatherDataHelpIntent = new Intent(getApplicationContext(), RefreshingWeatherDataHelp.class);
						startActivity(refreshingWeatherDataHelpIntent, null);
						break;
					case 5:
						Intent actionBarHelpIntent = new Intent(getApplicationContext(), ActionBarHelp.class);
						startActivity(actionBarHelpIntent, null);
						break;
					case 6:
						Intent changingSettingsHelpIntent = new Intent(getApplicationContext(), ChangingSettingsHelp.class);
						startActivity(changingSettingsHelpIntent, null);
						break;
					case 7:
						Intent sendFeedback = new Intent(Intent.ACTION_SEND);
						sendFeedback.setType("message/rfc882");
						sendFeedback.putExtra(Intent.EXTRA_EMAIL, new String[]{"theweatherlive@gmail.com"});
						sendFeedback.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
						startActivity(sendFeedback);
				}
			}
   
		});
       
	}
  
}


