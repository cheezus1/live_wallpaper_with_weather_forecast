package com.bk.theweatherlive;

import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

public class TheWeatherLiveWallpaperService extends GLWallpaperService {

	public TheWeatherLiveWallpaperService() {
		super();
	}
	
	public Engine onCreateEngine() {
		TheWeatherLiveEngine engine = new TheWeatherLiveEngine();
		return engine;
	}
	
	class TheWeatherLiveEngine  extends GLEngine {
		TheWeatherLiveRenderer renderer;
		
		public TheWeatherLiveEngine() {
			super();
			renderer = new TheWeatherLiveRenderer();
			setRenderer(renderer);
			setRenderMode(RENDERMODE_CONTINUOUSLY);
		}
		
		public void onDestroy() {
			super.onDestroy();
			
			if(renderer != null) {
				renderer.release();
			}
			renderer = null;
		}
	}
	
}
