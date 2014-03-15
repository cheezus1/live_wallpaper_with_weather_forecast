package com.bk.theweatherlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TEST_FILE = "testfile.txt";
	
	ContentResolver mContentResolver;
	private static final String TAG = "onPerformSync: "; //Used only for debugging!
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContentResolver = context.getContentResolver();
	}
	
	public SyncAdapter(
			Context context,
			boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		
		mContentResolver = context.getContentResolver();
	}
	
	public void onPerformSync(
			Account account,
			Bundle extras,
			String authority,
			ContentProviderClient provider,
			SyncResult syncResult) {
		final String units = extras.getString("units", "metric");
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					try {
						URL weatherApi = new URL("http://api.openweathermap.org/data/2.5/weather?q=Sofia&mode=xml&units=" + units);
						try {
							WeatherNowParser parser = new WeatherNowParser();
							String data = parser.parse(weatherApi.openStream());
							Log.d("TWL", data);
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(TEST_FILE, Context.MODE_PRIVATE));
					            Log.d("twl", "opened file");
					            outputStreamWriter.write(data);
					            outputStreamWriter.close();
					            Log.d("twl", "opened file");
							}
					        catch (IOException e) {
					            Log.e(TAG, "File write failed: " + e.toString());
					        } 
						} catch(IOException e) {
							e.printStackTrace();
						}
					} catch(MalformedURLException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
}
