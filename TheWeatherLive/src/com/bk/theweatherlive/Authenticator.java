package com.bk.theweatherlive;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

public class Authenticator extends AbstractAccountAuthenticator {
	
	public Authenticator(Context context) {
		super(context);
	}
	
	public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
			throw new UnsupportedOperationException();
	}
	
	public Bundle addAccount(
			AccountAuthenticatorResponse r,
			String s,
			String s2,
			String[] strings,
			Bundle bundle) throws NetworkErrorException {
		return null;
	}
	
	public Bundle confirmCredentials(
			AccountAuthenticatorResponse r,
			Account account,
			Bundle bundle) throws NetworkErrorException {
		return null;
	}
	
	public Bundle getAuthToken(
			AccountAuthenticatorResponse r,
			Account account,
			String s,
			Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	
	public String getAuthTokenLabel(String s) {
		throw new UnsupportedOperationException();
	}
	
	public Bundle updateCredentials(
			AccountAuthenticatorResponse r,
			Account account,
			String s,
			Bundle bundle) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}
	
	public Bundle hasFeatures(
			AccountAuthenticatorResponse r,
			Account account,
			String[] strings) throws NetworkErrorException {
		throw new UnsupportedOperationException();
	}

}
