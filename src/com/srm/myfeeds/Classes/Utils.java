package com.srm.myfeeds.Classes;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Class that adds utils functions in java
 */


public class Utils {
		
	
	/**
	 * Join arraylist elements with a string
	 * 
	 * @param arraylist
	 * @param glue
	 * @return String array joined
	 */
	public static String implode(ArrayList<String> arrayList, String glue){
			
		String output = "";
		
		if (arrayList.size() > 0){
			StringBuilder sb = new StringBuilder();
			sb.append(arrayList.get(0));
			
			for (int i=1;i<arrayList.size();i++){
				sb.append(glue);
				sb.append(arrayList.get(i));
			}
			
			output = sb.toString();
		}
		
		return output;		
	}
	
	
	/**
	 * Downloads an image and return a drawable
	 * 
	 * @param String Image url to download
	 * @return Drawable drawable to show
	 */
	
	public static Drawable downloadImage(String imageURL,int newHeight, int newWidth){
		
		URL url = null;
		
		try {
			url = new URL(imageURL);
		} catch (MalformedURLException e) {}
		
		if (url!=null){
			try{
				//set up & start an URL connection
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setDoInput(true);				
				conn.setConnectTimeout(10000);	
				conn.setReadTimeout(30000);
				conn.connect();
							
				//check that the element to download is an image
				if (!conn.getContentType().startsWith("image/")){					
					return null;
				}			
				
				//if connection has been successful
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {	
										
					//get image and return it
					InputStream is = conn.getInputStream();					
					Bitmap img = BitmapFactory.decodeStream(is);	
					img = Utils.getResizedBitmap(img, newHeight, newWidth);
					is.close();
				    return new BitmapDrawable(null, img);
				}
				
			}catch(IOException e){
				
			}catch(OutOfMemoryError error){
				Log.d("ERR MEMORY IMG", imageURL+" allocate");
			}
			
		}
		
		return null;
	}	
	
	
	/**
	 * Resize a bitmap (useful to avoid memory errors)
	 * @param Bitmap
	 * @param newHeight
	 * @param newWidth
	 * @return Bitmap
	 */
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		
		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

		return resizedBitmap;

		}
	
	/**
	 * Formats a date
	 * 
	 * @param datefrom string that contains date to format
	 * @param patternFrom pattern that has the original date
	 * @param patternTo pattern of the desired date format
	 * @return string formated date
	 */
	public static String formatDate(String datefrom, String patternFrom, String patternTo){
		
		Date date = null;
		DateFormat formatter =  new SimpleDateFormat(patternFrom, Locale.ENGLISH);
		
		try{
			date = formatter.parse(datefrom);
		}catch(ParseException e){}
		
		//If not valid date, no parse it (avoid error)
		String finalDate = null;
		if (date != null){
			date = new Date(date.getTime());
			formatter =  new SimpleDateFormat(patternTo);
			finalDate = formatter.format(date).toString();
		}
		
		return finalDate;
	}
	
	/**
	 * Extracts all image url contained in a HTML code block
	 * 
	 * @param String HTML block
	 * @return String[] Array that contains image URLS
	 */
	public static String[] extractImgFromHTML(String html){
		
		String[] urls = null;	
		
		Document doc = Jsoup.parse(html);
		Elements images = doc.getElementsByTag("img");		
		
		int count = images.size();
		
		if (count>0){		
			
			urls = new String[count];
						
			for (int i=0;i<count;i++){
				Element image = images.get(i);				
				urls[i] = image.attr("src");
			}			
		}
		
		return urls;
	}
	
}
