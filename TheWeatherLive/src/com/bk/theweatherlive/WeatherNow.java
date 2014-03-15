package com.bk.theweatherlive;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class WeatherNow extends FragmentActivity {

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
    ImageButton refreshButton;
    SharedPreferences preferences;
    Account mAccount;
    final Handler mHandler = new Handler();
    String toastText = "default text";
    
    public static final String AUTHORITY = "com.bk.theweatherlive.provider";
    public static final String ACCOUNT_TYPE = "example.com";
    public static final String ACCOUNT = "Weather Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_now);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        initRefreshButton();
        
        mAccount = CreateSyncAccount(this);
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

    public void initRefreshButton() {
    	refreshButton = (ImageButton) findViewById(R.id.refreshButton);
    	final View updateProgress = findViewById(R.id.updateProgress);
    	
    	refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// THIS IS A PLACEHOLDER CODE! IT WILL BE REPLACED IN LATER VERSIONS!
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateProgress.setVisibility(View.VISIBLE);
							}
						});
						Bundle settingsBundle = new Bundle();
						settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
						settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
						ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
						while(ContentResolver.isSyncPending(mAccount, AUTHORITY) || ContentResolver.isSyncActive(mAccount, AUTHORITY)){}
						//String toastText = "default text";
						try {
				            InputStream inputStream = openFileInput("testfile.txt");
				             
				            if ( inputStream != null ) {
				                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				                StringBuilder stringBuilder = new StringBuilder();
				                 
				                while ( (toastText = bufferedReader.readLine()) != null ) {
				                    stringBuilder.append(toastText);
				                }
				                 
				                inputStream.close();
				                toastText = stringBuilder.toString();
				            }
				        }
				        catch (FileNotFoundException e) {
				            
				        } catch (IOException e) {
				            
				        }
						
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateProgress.setVisibility(View.INVISIBLE);
							}
						});
						mHandler.post(mUpdateResults);
					}
				});
				thread.start();
			}
    	});
    }
    
    final Runnable mUpdateResults = new Runnable() {
		@Override
		public void run() {
			updateResultsInUi();
		}
    };
    
    private void updateResultsInUi() {
    	Toast.makeText(WeatherNow.this, toastText, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_now, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.action_settings:
    			Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
    			startActivity(settingsActivityIntent, null);
    			return true;
    		case R.id.action_help:
    			Intent helpActivityIntent = new Intent(this, HelpActivity.class);
    			startActivity(helpActivityIntent, null);
    			return true;
    		case R.id.action_about:
    			new AlertDialog.Builder(this)
    			.setTitle(WeatherNow.this.getResources().getString(R.string.app_name))
    			.setMessage("Live wallpaper with weather forecast.")
    			.setIcon(R.drawable.ic_launcher)
    			.show();
    			return true;
    		default:
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
            Fragment fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
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
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_weather_now_dummy, container, false);
            //Remove the numbers of the tabs
           /* TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
    }

}
