package com.bk.theweatherlive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
	
	private Authenticator mAuthenticator;
	
	public void onCreate() {
		mAuthenticator = new Authenticator(this);
	}
	
	public IBinder onBind(Intent intent) {
		return mAuthenticator.getIBinder();
	}

}
