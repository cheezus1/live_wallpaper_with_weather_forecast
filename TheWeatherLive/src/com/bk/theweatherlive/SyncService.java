package com.bk.theweatherlive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

	private static SyncAdapter sSyncAdapter = null;
	
	private static final Object sSyncAdapterLock = new Object();
	
	public void onCreate() {
		synchronized(sSyncAdapterLock) {
			if(sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}
	
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
	
}
