package com.bk.theweatherlive;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class StubProvider extends ContentProvider {
	
	public boolean onCreate() {
		return true;
	}
	
	public String getType() {
		return new String();
	}
	
	public Cursor query(
			Uri uri,
			String[] projection,
			String selection,
			String[] selectionArgs,
			String sortOrder) {
		return null;
	}
	
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}
	
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}
	
	public int update(
			Uri uri,
			ContentValues values,
			String selection,
			String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
