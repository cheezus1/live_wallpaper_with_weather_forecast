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
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(NOW_FILE, Context.MODE_PRIVATE));
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
				try {
					try {
						URL hourlyWeatherApi = new URL("http://api.openweathermap.org/data/2.5/forecast?q=Sofia&mode=xml&units=" + units);
						try {
							HourlyParser parser = new HourlyParser();
							String hourlyData = parser.parse(hourlyWeatherApi.openStream());
							Log.d("TWL", hourlyData);
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(HOURLY_FILE, Context.MODE_PRIVATE));
					            Log.d("twl", "opened file");
					            outputStreamWriter.write(hourlyData);
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
				try {
					try {
						URL forecastWeatherApi = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Sofia&mode=xml&units=" + units + "&cnt=" + forecastDays);
						try {
							ForecastParser parser = new ForecastParser();
							String forecastData = parser.parse(forecastWeatherApi.openStream(), units);
							Log.d("TWL", forecastData);
							try {
					            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(FORECAST_FILE, Context.MODE_PRIVATE));
					            Log.d("twl", "opened file");
					            outputStreamWriter.write(forecastData);
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
