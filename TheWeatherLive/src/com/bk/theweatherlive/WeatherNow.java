package com.bk.theweatherlive;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class WeatherNow extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, android.location.LocationListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    LocationClient mLocationClient;
    LocationManager mLocationManager;
    Location mCurrentLocation;
    ImageButton refreshButton;
    SharedPreferences preferences;
    Account mAccount;
    final Handler mHandler = new Handler();
    CurrentData now = new CurrentData();
    ArrayList<ForecastData> hourly = new ArrayList<ForecastData>();
    ArrayList<ForecastData> forecast = new ArrayList<ForecastData>();
    RelativeLayout bg;
    private WallpaperManager mWallpaperManager;
    private Animation refreshButtonAnimation;
    
    public static final String AUTHORITY = "com.bk.theweatherlive.provider";
    public static final String ACCOUNT_TYPE = "example.com";
    public static final String ACCOUNT = "Weather Update";
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000 * 10;
    private static long MIN_TIME_BETWEEN_UPDATES;
    
    public static final String ACTION_FINISHED_SYNC = "com.bk.theweatherlive.ACTION_FINISHED_SYNC";
    private static IntentFilter syncIntentFilter = new IntentFilter(ACTION_FINISHED_SYNC);
    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateInUi();
		}
    };

    public static final String ACTION_OPENED_HOURLY = "com.bk.theweatherlive.ACTION_OPENED_HOURLY";
    private static IntentFilter hourlyIntentFilter = new IntentFilter(ACTION_OPENED_HOURLY);
    private BroadcastReceiver hourlyBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateHourlyData();
		}
    };
    
    public static final String ACTION_OPENED_FORECAST = "com.bk.theweatherlive.ACTION_OPENED_FORECAST";
    private static IntentFilter forecastIntentFilter = new IntentFilter(ACTION_OPENED_FORECAST);
    private BroadcastReceiver forecastBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateForecastData();
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_now);
        bg = (RelativeLayout)findViewById(R.id.main_activity_layout);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationClient = new LocationClient(this, this, this);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * Long.parseLong(preferences.getString("update_frequency", "1"));
        
       // initRefreshButton();
        refreshButtonAnimation = AnimationUtils.loadAnimation(this, R.layout.rotate_clockwise);
        
        mAccount = CreateSyncAccount(this);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	mLocationClient.connect();
    	servicesConnected();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	registerReceiver(syncBroadcastReceiver, syncIntentFilter);
    	registerReceiver(hourlyBroadcastReceiver, hourlyIntentFilter);
    	registerReceiver(forecastBroadcastReceiver, forecastIntentFilter);
    }
    
    @Override
    protected void onPause() {
    	unregisterReceiver(syncBroadcastReceiver);
    	unregisterReceiver(hourlyBroadcastReceiver);
    	unregisterReceiver(forecastBroadcastReceiver);
    	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	mLocationClient.disconnect();
    	super.onStop();
    }
    
    public static Account CreateSyncAccount(Context context) {
    	Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
    	AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
    	if(accountManager.addAccountExplicitly(newAccount, null, null)) {
    		return newAccount;
    	} else {
    		//error log
    	}
    	return null;
    }

    /*public void initRefreshButton() {
    	refreshButton = (ImageButton) findViewById(R.id.refreshButton);
    	
    	refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sync();
			}
    	});
    }*/
    
    public void sync() {
    	final View updateProgress = findViewById(R.id.updateProgress);
    	Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//updateProgress.setVisibility(View.VISIBLE);
					}
				});
				mCurrentLocation = mLocationClient.getLastLocation();
				Bundle settingsBundle = new Bundle();
				settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
				settingsBundle.putString("units", preferences.getString("units", "metric"));
				settingsBundle.putString("forecast_days", preferences.getString("forecast_days", "14"));
				settingsBundle.putDouble("latitude", mCurrentLocation.getLatitude());
				settingsBundle.putDouble("longitude", mCurrentLocation.getLongitude());
				ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
			}
		});
		thread.start();
    }
    
    private void updateInUi() {
    	Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < 3; i++) {
					switch(i) {
					case 0:
						try {
				            InputStream inputStream = openFileInput("hourly.txt");
				             
				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                String buffer = "";
				                hourly.clear();
				                int index = 0;
				                
				                while ( (buffer = bufferedReader.readLine()) != null ) {
				                    ForecastData temporary = new ForecastData();
				                	temporary.setWeatherCode(Integer.parseInt(buffer));
				                    temporary.setTime(bufferedReader.readLine());
				                    temporary.setTemp(bufferedReader.readLine());
				                    temporary.setMaxTemp(bufferedReader.readLine());
				                    temporary.setMinTemp(bufferedReader.readLine());
				                    temporary.setWeatherString(bufferedReader.readLine());
				                    hourly.add(index, temporary);
				                    index++;
				                }
				                 
				                inputStream.close();
				            }
				        } catch (FileNotFoundException e) {
				            
				        } catch (IOException e) {
				            
				        }
					case 1:
						try {
				            InputStream inputStream = openFileInput("now.txt");
				             
				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                 
				                now.setWeatherCode(Integer.parseInt(bufferedReader.readLine()));
				                now.setCityName(bufferedReader.readLine());
				                now.setTemp(bufferedReader.readLine());
				                now.setMaxTemp(bufferedReader.readLine());
				                now.setMinTemp(bufferedReader.readLine());
				                now.setHumidity(bufferedReader.readLine());
				                now.setPressure(bufferedReader.readLine());
				                now.setWind(bufferedReader.readLine());
				                now.setWeatherString(bufferedReader.readLine());
				                 
				                inputStream.close();
				            }
				        } catch (FileNotFoundException e) {
				            
				        } catch (IOException e) {
				            
				        }
					case 2:
						try {
				            InputStream inputStream = openFileInput("forecast.txt");
				             
				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                String buffer = "";
				                forecast.clear();
				                int index = 0;
				                 
				                while ( (buffer = bufferedReader.readLine()) != null ) {
				                    ForecastData temporary = new ForecastData();
				                	temporary.setWeatherCode(Integer.parseInt(buffer));
				                    temporary.setTime(bufferedReader.readLine());
				                    temporary.setTemp(bufferedReader.readLine());
				                    temporary.setMaxTemp(bufferedReader.readLine());
				                    temporary.setMinTemp(bufferedReader.readLine());
				                    temporary.setWeatherString(bufferedReader.readLine());
				                    forecast.add(index, temporary);
				                    index++;
				                }
				                 
				                inputStream.close();
				            }
				        } catch (FileNotFoundException e) {
				            
				        } catch (IOException e) {
				            
				        }
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final View updateProgress = findViewById(R.id.updateProgress);
						//mViewPager.setCurrentItem(1);
						if(mViewPager.getCurrentItem() != 2) {
							updateHourlyData();
						}
						updateCurrentData();
						if(mViewPager.getCurrentItem() != 0) {
							updateForecastData();
						}
						//RelativeLayout bg = (RelativeLayout)findViewById(R.layout.activity_weather_now);
						/*if(bg == null) {
							Log.d("twl", "FUCK JAVA");
						}
						mWallpaperManager = WallpaperManager.getInstance(getApplicationContext());
						try {
							if(now.contains("CLEAR")) {
								bg.setBackgroundResource(R.drawable.background_sunny);
								mWallpaperManager.setResource(R.drawable.background_sunny);
							} else if(now.contains("OVERCAST")) {
								bg.setBackgroundResource(R.drawable.background_overcast);
								mWallpaperManager.setResource(R.drawable.background_overcast);
							} else if(now.contains("CLOUD")) {
								bg.setBackgroundResource(R.drawable.background_cloudy);
								mWallpaperManager.setResource(R.drawable.background_cloudy);
							} else if(now.contains("RAIN")) {
								bg.setBackgroundResource(R.drawable.background_raining);
								mWallpaperManager.setResource(R.drawable.background_raining);
							} else if(now.contains("DRIZZLE")) {
								bg.setBackgroundResource(R.drawable.background_raining);
								mWallpaperManager.setResource(R.drawable.background_raining);
							} else if(now.contains("SNOW")) {
								bg.setBackgroundResource(R.drawable.background_snowing);
								mWallpaperManager.setResource(R.drawable.background_snowing);
							} else if(now.contains("THUNDERSTORM")) {
								bg.setBackgroundResource(R.drawable.background_thunderstorm);
								mWallpaperManager.setResource(R.drawable.background_thunderstorm);
							} else {
								bg.setBackgroundResource(R.drawable.background_overcast);
								mWallpaperManager.setResource(R.drawable.background_overcast);
							}
						} catch(IOException e) {
							Toast.makeText(getApplicationContext(), "Error while changing the wallpaper", Toast.LENGTH_SHORT).show();
						}*/
						//updateProgress.setVisibility(View.INVISIBLE);
						findViewById(R.id.action_refresh).clearAnimation();
					}
				});
			}
    	});
    	thread1.start();
    }
    
    private void updateHourlyData() {
    	if (!(hourly.isEmpty())) {
        	ListView hourlyListView = (ListView) findViewById(R.id.hourlyListView);
        	MyArrayAdapter hourlyArrayAdapter = new MyArrayAdapter(this, hourly);
        	hourlyListView.setAdapter(hourlyArrayAdapter);
    	}
    }
    
    private void updateCurrentData() {
		((TextView) findViewById(R.id.nowCityName)).setText(now.getCityName());
		((TextView) findViewById(R.id.nowTemp)).setText(now.getTemp());
		((TextView) findViewById(R.id.nowMaxTemp)).setText(now.getMaxTemp());
		((TextView) findViewById(R.id.nowMinTemp)).setText(now.getMinTemp());
		((TextView) findViewById(R.id.nowCondition)).setText(now.getWeatherString());
		((TextView) findViewById(R.id.nowHumidity)).setText("Humidity: " + now.getHumidity());
		((TextView) findViewById(R.id.nowPressure)).setText("Pressure: " + now.getPressure());
		((TextView) findViewById(R.id.nowWind)).setText("Wind: " + now.getWind());
    }
    
    private void updateForecastData() {
    	if(!(forecast.isEmpty())) {
    		ListView forecastListView = (ListView) findViewById(R.id.forecastListView);
        	MyArrayAdapter forecastArrayAdapter = new MyArrayAdapter(this, forecast);
        	forecastListView.setAdapter(forecastArrayAdapter);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_now, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	if (itemId == R.id.action_refresh) {
    		sync();
    		findViewById(R.id.action_refresh).startAnimation(refreshButtonAnimation);
    		return true;
    	} else if (itemId == R.id.action_settings) {
			Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsActivityIntent, null);
			return true;
		} else if (itemId == R.id.action_help) {
			Intent helpActivityIntent = new Intent(this, HelpActivity.class);
			startActivity(helpActivityIntent, null);
			return true;
		} else if (itemId == R.id.action_about) {
			new AlertDialog.Builder(this)
			.setTitle(WeatherNow.this.getResources().getString(R.string.app_name))
			.setMessage("Live wallpaper with weather forecast.")
			.setIcon(R.drawable.ic_launcher)
			.show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }
    
    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
        	switch(position) {
        	case 0:
	            Fragment fragment1 = new HourlySectionFragment();
	            Bundle args1 = new Bundle();
	            args1.putInt(HourlySectionFragment.ARG_SECTION_NUMBER, position + 1);
	            fragment1.setArguments(args1);
	            //getBaseContext().sendBroadcast(new Intent(WeatherNow.ACTION_OPENED_HOURLY));
	            return fragment1;
        	case 1:
        		Fragment fragment2 = new NowSectionFragment();
        		Bundle args2 = new Bundle();
        		args2.putInt(NowSectionFragment.ARG_SECTION_NUMBER,  position + 1);
        		fragment2.setArguments(args2);
        		return fragment2;
        	case 2:
        		Fragment fragment3 = new ForecastSectionFragment();
        		Bundle args3 = new Bundle();
        		args3.putInt(ForecastSectionFragment.ARG_SECTION_NUMBER,  position + 1);
        		fragment3.setArguments(args3);
        		//getBaseContext().sendBroadcast(new Intent(WeatherNow.ACTION_OPENED_FORECAST));
        		return fragment3;
        	}
        	return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return (getString(R.string.title_section3_part1) + " " + preferences.getString("forecast_days", "14") + " " + getString(R.string.title_section3_part2)).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class HourlySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        

        public HourlySectionFragment() {
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weather_now_hourly, container, false);
            //Remove the numbers of the tabs
           /* TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/
            getActivity().sendBroadcast(new Intent(WeatherNow.ACTION_OPENED_HOURLY));
            return rootView;
        }
    }
    
    public static class NowSectionFragment extends Fragment {
    	
    	public static final String ARG_SECTION_NUMBER = "section_number";
    	
    	public NowSectionFragment() {}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		View rootView = inflater.inflate(R.layout.fragment_weather_now_now, container, false);
    		return rootView;
    	}
    }
    
    public static class ForecastSectionFragment extends Fragment {
    	
    	public static final String ARG_SECTION_NUMBER = "section_number";
    	
    	public ForecastSectionFragment() {}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		View rootView = inflater.inflate(R.layout.fragment_weather_now_forecast, container, false);
    		getActivity().sendBroadcast(new Intent(WeatherNow.ACTION_OPENED_FORECAST));
    		return rootView;
    	}    	
    }

    public static class ErrorDialogFragment extends DialogFragment {
    	
    	private Dialog mDialog;
    	
    	public ErrorDialogFragment() {
    		super();
    		mDialog = null;
    	}
    	
    	public void setDialog(Dialog dialog) {
    		mDialog = dialog;
    	}
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		return mDialog;
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	case CONNECTION_FAILURE_RESOLUTION_REQUEST:
    		switch(resultCode) {
    		case Activity.RESULT_OK:
    			mLocationClient.connect();
    			break;
    		}
    	}
    }
    
    public boolean servicesConnected() {
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(ConnectionResult.SUCCESS == resultCode) {
    		Log.d("Location Updates", "Google Play Services Available");
    		return true;
    	} else {
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
    		if(dialog != null) {
    			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
    			errorFragment.setDialog(dialog);
    			errorFragment.show(this.getFragmentManager(), "Location updates");
    		}
    		return false;
    	}
    }

    @Override
    public void onConnected(Bundle dataBundle) {
    	Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    	} else if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
    		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    	}
    	if(preferences.getBoolean("enable_update_on_launch", true)) {
        	sync(); //Commented for testing the code in an emulator
        } else {
        	updateInUi();
        }
    }
    
    @Override
    public void onDisconnected() {
    	Toast.makeText(this,  "Disconnected. Please re-connect", Toast.LENGTH_LONG).show();
    }
    
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	if(connectionResult.hasResolution()) {
    		try {
    			connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
    		} catch(IntentSender.SendIntentException e) {
    			e.printStackTrace();
    		}
    	} else {
    		showDialog(connectionResult.getErrorCode());
    	}
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	

  







}
