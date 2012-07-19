package com.srm.myfeeds.Classes;


import android.graphics.drawable.Drawable;


public class Entry {
	
	private String title;
	private String date;
	private String imageUrl;
	private Drawable image;
	private String url;
	
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setDate(String pubDate){				
		this.date = Utils.formatDate(pubDate, "EEE, dd MMM yyyy HH:mm:ss zzz", "dd/MM/yyy HH:mm");		
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
			
		this.image = Utils.downloadImage(imageUrl, 45, 55);
	}
	
	public String getImageUrl(){
		return this.imageUrl;
	}	
	
	public Drawable getDrawable(){
		return this.image;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return this.url;
	}
}
