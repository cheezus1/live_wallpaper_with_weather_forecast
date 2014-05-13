package com.bk.theweatherlive;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class TheWeatherLiveWallpaperService extends GLWallpaperService {

	public TheWeatherLiveWallpaperService() {
		super();
	}
	
	public Engine onCreateEngine() {
		TheWeatherLiveEngine engine = new TheWeatherLiveEngine();
		return engine;
	}
	
	class TheWeatherLiveEngine  extends GLEngine implements SensorEventListener {
		TheWeatherLiveRenderer renderer;
		
		public TheWeatherLiveEngine() {
			super();
			renderer = new TheWeatherLiveRenderer();
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}
		
		public void onDestroy() {
			
			sensorManager.unregisterListener(this);
			
			if(renderer != null) {
				renderer.release();
			}
			renderer = null;
			
			setTouchEventsEnabled(false);
			
			super.onDestroy();
		}
		
		private SensorManager sensorManager;
		
		@Override
		public void onTouchEvent(MotionEvent event) {
			super.onTouchEvent(event);
			renderer.onTouchEvent(event);
		}
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
			setTouchEventsEnabled(true);
			
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			Sensor orientationSensor = sensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
			sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
		}
		
		public void onSensorChanged(SensorEvent event) {
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	}
	
}
