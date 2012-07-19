package com.srm.myfeeds.Models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "feeds.db";
	private static final int DB_VERSION = 1;
	
	public Database(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE categories (" +
				"id integer primary key autoincrement," +
				"name text" +
				");");
		
		db.execSQL("CREATE TABLE feeds (" +
				"id integer primary key autoincrement," +
				"title text," +
				"category integer," +
				"url text" +
				");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}	
}
