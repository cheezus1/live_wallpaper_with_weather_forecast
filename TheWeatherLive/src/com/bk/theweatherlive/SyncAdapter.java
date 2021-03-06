package com.bk.theweatherlive;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String NOW_FILE = "now.txt";
	private static final String HOURLY_FILE = "hourly.txt";
	private static final String FORECAST_FILE = "forecast.txt";
	
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
		final String forecastDays = extras.getString("forecast_days", "14");
		final double latitude = extras.getDouble("latitude");
		final double longitude = extras.getDouble("longitude");
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					try {
						URL weatherApi = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&mode=xml&units=" + units);
						try {
							WeatherNowParser parser = new WeatherNowParser();
							String data = parser.parse(weatherApi.openStream());
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(NOW_FILE, Context.MODE_PRIVATE));
					            outputStreamWriter.write(data);
					            outputStreamWriter.close();
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
				try {
					try {
						URL hourlyWeatherApi = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&mode=xml&units=" + units);
						try {
							HourlyParser parser = new HourlyParser();
							String hourlyData = parser.parse(hourlyWeatherApi.openStream());
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(HOURLY_FILE, Context.MODE_PRIVATE));
					            outputStreamWriter.write(hourlyData);
					            outputStreamWriter.close();
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
				try {
					try {
						URL forecastWeatherApi = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&mode=xml&units=" + units + "&cnt=" + forecastDays);
						try {
							ForecastParser parser = new ForecastParser();
							String forecastData = parser.parse(forecastWeatherApi.openStream(), units);
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(FORECAST_FILE, Context.MODE_PRIVATE));
					            outputStreamWriter.write(forecastData);
					            outputStreamWriter.close();
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
				getContext().sendBroadcast(new Intent(WeatherNow.ACTION_FINISHED_SYNC));
			}
			
		});
		thread.start();
	}
	
}
