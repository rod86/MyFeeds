package com.srm.myfeeds.Activities;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.srm.myfeeds.R;
import com.srm.myfeeds.Models.FeedsModel;
import com.srm.myfeeds.Models.CategoriesModel;
import com.srm.myfeeds.Classes.FeedParser;

public class FeedForm extends Activity {
	
	private int categoryId;			//selected category ID in category listview
	private int feedId;				//current feed id (only in edit mode)
	protected FeedsModel FeedsModel; //FeedsModel instance
	protected CategoriesModel CategoriesModel; //categories model instance
	private EditText titleText;		//form field title
	private EditText urlText;		//form field url
	private Spinner categorySpinner;//form field category
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.feed_form); 
        
        //get selected categoryId
        Bundle extras = getIntent().getExtras();
        categoryId = extras.getInt("categoryId");       
        feedId = extras.getInt("feedId");    
                
        //get field views
        TextView formTitle = (TextView)findViewById(R.id.formTitle);
        titleText = (EditText)findViewById(R.id.title);
        urlText = (EditText)findViewById(R.id.url);        
        urlText.setOnFocusChangeListener(urlListener);        
        categorySpinner = (Spinner)findViewById(R.id.category); 
        
        //init models
    	CategoriesModel = new CategoriesModel(this);  
    	FeedsModel = new FeedsModel(this);
    	
    	//Get categories to fill spinner  
    	String[] categories = CategoriesModel.getCategories();       
        
        //create adapter and set it to category spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);               
        categorySpinner.setAdapter(dataAdapter);
        
        //get name for getting position to set spinner's selected item
    	String name = CategoriesModel.getCategoryName(categoryId);  
        categorySpinner.setSelection(dataAdapter.getPosition(name));
        
        //Assign onclick listener on cancel button
        Button btnCancel = (Button)findViewById(R.id.ko);
        
        //on click cancel, end current activity and go to previous activity
        btnCancel.setOnClickListener(new OnClickListener() {    		
    		@Override
    		public void onClick(View v) {
    			finish();
    		}
    	});
        
        //Assign onclick listener on accept button
        Button btnAccept = (Button)findViewById(R.id.ok);
               
        //set header, field values and accept button event according if we are editing or adding
        if (feedId!=0) {//if we are in edit mode
        	formTitle.setText(getString(R.string.edit_feed));
        	
        	HashMap<String,String> feed = FeedsModel.getFeed(feedId);
        	titleText.setText(feed.get("title"));
        	urlText.setText(feed.get("url"));
        	
        	//on click accept, validate and update feed
            btnAccept.setOnClickListener(editFeed); 
        }else{//add mode
        	formTitle.setText(getString(R.string.new_feed));
        	
        	//on click accept, validate and insert new feed
            btnAccept.setOnClickListener(addFeed); 
        }
        
        
    }
    
    //on url change get title from feed and set it in title field
    OnFocusChangeListener urlListener = new OnFocusChangeListener() {		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			//On losing focus
			if (!hasFocus){
				
				String url = urlText.getText().toString();
				
				//if url is empty or equals prefix http//:
				if (url.equals("") || url.equals("http://")){
					urlText.setText("http://");
					Log.d("ERR VAL", "url nula");
					return;
				}
				
				//if is not a valid url
				if (!URLUtil.isValidUrl(url)){
					Log.d("ERR VAL", "INVALID URL");
					return;
				}
				
				 
				//Get title from feed
				FeedParser parser = new FeedParser(url);
				String title = parser.getTitle();
				
				if (title != null && !title.equals("") && titleText.getText().toString()!=""){
					titleText.setText(title);
				}
		
			}
		}
	};
    
    
    //onlick event for ok on adding a new feed
	OnClickListener addFeed = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			//get field values
			String title = titleText.getText().toString().trim();
			String url = urlText.getText().toString().trim();
			String categoryName = categorySpinner.getSelectedItem().toString();
				
			//Get category ID
			Integer category = CategoriesModel.findByName(categoryName);
	
			//if title is empty
			if (title.equals("")){
				Toast.makeText(getApplicationContext(), getString(R.string.title_required), Toast.LENGTH_SHORT).show();
				return;
			}
	
			//check if there's any feed with the same title
			boolean isUnique = FeedsModel.isUniqueName(title,0);			
			if (!isUnique){
				String msg = getString(R.string.title_exists).replace("{title}", title);
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				return;
			}
	
			//if url is empty
			if (url.equals("")){
				Toast.makeText(getApplicationContext(), getString(R.string.url_required), Toast.LENGTH_SHORT).show();
				return;
			}
	
			//check if is a valid URL
			if (!URLUtil.isValidUrl(url)){
				Toast.makeText(getApplicationContext(), getString(R.string.url_valid), Toast.LENGTH_SHORT).show();
				return;
			}
	
			//add feed
			FeedsModel.add(title, url, category);
	
			//return to feed list activity
			finish();			
		}
	};
	
	//onlick event for ok on editing feed
	OnClickListener editFeed = new OnClickListener() {		
			@Override
			public void onClick(View v) {				
				//get field values
				String title = titleText.getText().toString().trim();
				String url = urlText.getText().toString().trim();
				String categoryName = categorySpinner.getSelectedItem().toString();
				
				//Get category ID
				Integer category = CategoriesModel.findByName(categoryName);
				
				//if title is empty
				if (title.equals("")){
					Toast.makeText(getApplicationContext(), getString(R.string.title_required), Toast.LENGTH_SHORT).show();
					return;
				}
				
				//check if there's any feed with the same title
				boolean isUnique = FeedsModel.isUniqueName(title, feedId);			
				if (!isUnique){
					String msg = getString(R.string.title_exists).replace("{title}", title);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
					return;
				}				
				
				//if url is empty
				if (url.equals("")){
					Toast.makeText(getApplicationContext(), getString(R.string.url_required), Toast.LENGTH_SHORT).show();
					return;
				}
				
				//check if is a valid URL
				if (!URLUtil.isValidUrl(url)){
					Toast.makeText(getApplicationContext(), getString(R.string.url_valid), Toast.LENGTH_SHORT).show();
					return;
				}				
				
				//update feed
				FeedsModel.update(feedId, title, url, category);
				
				//return to feed list activity
				finish();			
			}
		};
    
}
