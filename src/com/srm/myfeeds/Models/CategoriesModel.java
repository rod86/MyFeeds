package com.srm.myfeeds.Models;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class CategoriesModel extends Database{
	

	public CategoriesModel(Context context) {
		super(context);		
	}
	
	
	
	/**
	 * gets All categories
	 * @return String[] Array of names
	 */
	public String[] getCategories(){
		
		SQLiteDatabase db = this.getReadableDatabase();		
		Cursor cursor = db.query("categories", null, null, null, null, null, null);		
		
		String[] rows = new String[cursor.getCount()];		
		
		while (cursor.moveToNext()){			
			String name = cursor.getString(cursor.getColumnIndex("name"));					
			rows[cursor.getPosition()] = name;
		}
		
		cursor.close();
		db.close();
		
		return rows; 
	}
	
	/**
	 * Gets the category name's id 
	 * @param String name
	 * @return	Integer id
	 */
	public Integer findByName(String name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = {"id"};
		String[] args = {name};
		
		Cursor cursor = db.query("categories", fields, "name=?", args, null, null, null);
		
		Integer id = null;
		
		if (cursor.moveToFirst()){
			id = cursor.getInt(cursor.getColumnIndex("id"));			
		}
		
		cursor.close();
		db.close();

		return id;
	}
	
	/**
	 * Gets category name from id
	 * @param long id
	 * @return String name
	 */
	public String getCategoryName(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		String[] fields = {"name"};
		String[] args = {String.valueOf(id)};
		
		Cursor cursor = db.query("categories", fields, "id=?", args, null, null, null);
		
		String name = "";
		
		if (cursor.moveToFirst()){
			name = cursor.getString(cursor.getColumnIndex("name"));
		}
		
		cursor.close();
		db.close();
		
		return name;
	}
	
	/**
	 * Returns true if a supplied name doesn't exists in db (omitting it)
	 * @param name A name to check if exists
	 * @param categoryId Item's id to ignore
	 * @return boolean
	 */
	public boolean isUniqueName(String name, int categoryId){
		boolean isUnique;
		SQLiteDatabase db = this.getReadableDatabase(); 
		
		String where = "id!=? AND name=?";
		String[] args = {String.valueOf(categoryId), name};
		
		Cursor cursor = db.query("categories", null, where, args, null, null, null);	
		
		//if there's results, the name has been added before so isn't unique
		if (cursor.getCount() > 0) isUnique = false;
		else isUnique = true;
				
		cursor.close();
		db.close();
		
		return isUnique;
	}
	
	/**
	 * Insert new category
	 * @param name
	 */
	public void add(String name){		
		SQLiteDatabase db = this.getWritableDatabase();		
        ContentValues categoria = new ContentValues();
        categoria.put("name", name);
        db.insert("categories", null, categoria);	
        db.close();
	}
	
	/**
	 * Update category
	 * @param categoryId
	 * @param name
	 */
	public void update(int categoryId, String name){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues categoria = new ContentValues();
		categoria.put("name", name);		 
		String[] args = new String[]{String.valueOf(categoryId)};
		db.update("categories", categoria, "id=?", args);
		db.close();
	}
	
	/**
	 * Delete category
	 * @param id
	 */
	public void delete(long id){		
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("categories", "id="+id, null);
        db.close();
	}
	
	
}
