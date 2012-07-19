package com.srm.myfeeds.Models;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.srm.myfeeds.Classes.Utils;

public class FeedsModel extends Database {

	public FeedsModel(Context context) {
		super(context);		
	}

	
	public ArrayList<HashMap<String, String>> getFeedsByCategory(int categoryId){
		
		SQLiteDatabase db = this.getReadableDatabase();		
		ArrayList<HashMap<String,String>> feeds = new ArrayList<HashMap<String, String>>();
		
		//Get feeds that belongs to categoryId			
		String[] args = {String.valueOf(categoryId)};		
		Cursor cursor = db.query("feeds", null, "category=?",args, null, null, null);
			
		while (cursor.moveToNext()){			
			//for each item generate an associative hashmap and add to array
			HashMap<String,String> row = new HashMap<String, String>();
			row.put("title", cursor.getString(cursor.getColumnIndex("title")));
			row.put("url", cursor.getString(cursor.getColumnIndex("url")));
			
			feeds.add(row);
		}
		
		cursor.close();
		db.close();	
		
		return feeds;
	}
	
	/**
	 * Returns true if a supplied name doesn't exists in db. If you pass an Item id, this id will be ignored
	 * @param name A name to check if exists
	 * @param feedId Item's id to ignore
	 * @return boolean
	 */
	public boolean isUniqueName(String title, int feedId){
		
		boolean isUnique;		
		ArrayList<String> whereClauses = new ArrayList<String>();
		ArrayList<String> whereValues = new ArrayList<String>();
		
		//add name to where clause
		whereClauses.add("title=?");
		whereValues.add(title);
		
		//if is feedId, ignore this feed
		if (feedId != 0){
			whereClauses.add("id!=?");
			whereValues.add(String.valueOf(feedId));
		}		
		
		String where = Utils.implode(whereClauses, " AND ");
		//convert values to string array
		String [] args = whereValues.toArray(new String[whereValues.size()]);
		
		SQLiteDatabase db = this.getReadableDatabase(); 
		Cursor cursor = db.query("feeds", null, where, args, null, null, null);
		
		//if there's results, the name has been added before so isn't unique
		if (cursor.getCount() > 0) isUnique = false;
		else isUnique = true;
		
		cursor.close();
		db.close();
		
		return isUnique;		
	}
	
	
	/**
	 * Gets the feed id by feed's title 
	 * @param String title
	 * @return	Integer id
	 */
	public Integer findByName(String name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = {"id"};
		String[] args = {name};
		
		Cursor cursor = db.query("feeds", fields, "title=?", args, null, null, null);
		
		Integer id = null;
		
		if (cursor.moveToFirst()){
			id = cursor.getInt(cursor.getColumnIndex("id"));			
		}
		
		cursor.close();
		db.close();

		return id;
	}
	
	/**
	 * Gets feed from id
	 * @param long id
	 * @return String name
	 */
	public HashMap<String,String> getFeed(int id){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = {"title","url"};
		String[] args = {String.valueOf(id)};
		
		Cursor cursor = db.query("feeds", fields, "id=?", args, null, null, null);
		
		HashMap<String,String> row = null;
		if (cursor.moveToFirst()){						
			row = new HashMap<String, String>();
			row.put("title", cursor.getString(cursor.getColumnIndex("title")));
			row.put("url", cursor.getString(cursor.getColumnIndex("url")));
		}
		
		cursor.close();
		db.close();
		
		return row;
	}
	
	/**
	 * Insert new feed
	 * @param title
	 * @param url
	 * @param category
	 */
	public void add(String title, String url, int category){		
		SQLiteDatabase db = this.getWritableDatabase();		
        ContentValues feed = new ContentValues();
        feed.put("title", title);
        feed.put("url", url);
        feed.put("category", category);
        db.insert("feeds", null, feed);	
        db.close();
	}
	
	/**
	 * Update feed
	 * @param id
	 * @param title
	 * @param url
	 * @param category
	 */
	public void update(int id, String title, String url, int category){
		SQLiteDatabase db = this.getWritableDatabase();				
		ContentValues feed = new ContentValues();
        feed.put("title", title);
        feed.put("url", url);
        feed.put("category", category);
        String[] args = new String[]{String.valueOf(id)};
        db.update("feeds", feed, "id=?", args);
		db.close();
	}
	
	/**
	 * Delete feed
	 * @param id
	 */
	public void delete(int id){		
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("feeds", "id="+id, null);
        db.close();
	}
	
	
	/**
	 * Removes feeds by category
	 * @param category
	 */
	public void deleteByCategory(int category){
		SQLiteDatabase db = this.getWritableDatabase();
        db.delete("feeds", "category="+category, null);
        db.close();
	}
}
