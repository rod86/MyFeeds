package com.srm.myfeeds.Activities;

import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.srm.myfeeds.R;
import com.srm.myfeeds.Models.FeedsModel;
import com.srm.myfeeds.Adapters.FeedsAdapter;

public class Feeds extends Activity {	
	
	private int categoryId;			//selected category ID
	protected ListView feedsList;	//Listview that contains list of feeds
	protected FeedsModel FeedsModel; //FeedsModel instance
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);        
        
        //get selected categoryId
        Bundle extras = getIntent().getExtras();
        categoryId = extras.getInt("categoryId");   
             
        //init vars
        feedsList = (ListView)findViewById(R.id.list);
        feedsList.setOnItemClickListener(feedListener);
        
        FeedsModel = new FeedsModel(this);              
                   
        refreshFeedsList();
        
      //associate contextual menu
        registerForContextMenu(feedsList);
    }
    
    //on showing again this activity (you come from other)
    public void onResume(){
    	super.onResume(); 
    	refreshFeedsList();
    }
    
  //on press menu button of device appears this menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	super.onCreateOptionsMenu(menu);
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    //on select any option of menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
    		
    		case R.id.add:    			
    			//Launch activity to add a feed
        		Intent feedForm = new Intent(Feeds.this, FeedForm.class);    	
    			feedForm.putExtra("categoryId", categoryId);
    			startActivity(feedForm);
    			return true;    		
    	}
    	
    	return false;
    }
	
    //contextual menu for each listview item
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);  
        
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;  
        //get title feed and set to header of context menu
        menu.setHeaderTitle(((TextView)info.targetView.findViewById(R.id.title)).getText());    
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ctx, menu);
    }
    
    //on select any option of menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
     
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo(); 
        
        //get title for getting item id
        String title = (String)((TextView)info.targetView.findViewById(R.id.title)).getText();        
    	int id = FeedsModel.findByName(title);
        	        
        switch (item.getItemId()) {
            case R.id.edit:
            	//Launch activity to add a feed
        		Intent feedForm = new Intent(Feeds.this, FeedForm.class);    	
    			feedForm.putExtra("categoryId", categoryId);
    			feedForm.putExtra("feedId", id);
    			startActivity(feedForm);
                return true;
                
            case R.id.delete:
            	deleteFeed(id);         	
                return true;
                
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    //event listener that triggers when user touch listview's item
    OnItemClickListener feedListener = new OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long id) {    		
    		
    		//get category id of selected category
    		int feedId = FeedsModel.findByName((String)((TextView)v.findViewById(R.id.title)).getText());
    		
    		//Launch activity that lists feeds of the selected category
    		Intent feedView = new Intent(Feeds.this, FeedView.class);    	
    		feedView.putExtra("feedId", feedId);
			startActivity(feedView);
    		
		}
    }; // end listener
    
    //call feed adapter and assign to feed list. Reload feeds list
    public void refreshFeedsList(){    	
    	
    	//load data and send them to adapter      	    	
    	FeedsAdapter adapter = new FeedsAdapter(this, FeedsModel.getFeedsByCategory(categoryId));
    	feedsList.setAdapter(adapter);
    	
    	//Set empty view that is displayed when listview hasn't items
    	TextView emptyView = (TextView)findViewById(R.id.listEmpty);
    	emptyView.setText(getString(R.string.feeds_list_empty));
		feedsList.setEmptyView(emptyView);		
    }
    
    //confirm and delete a feed
    public void deleteFeed(final int feedId){
    	//get title
		HashMap<String,String> feed = FeedsModel.getFeed(feedId);	
    	final String title = feed.get("title");    	
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.delete); // title bar string
        builder.setMessage(getString(R.string.feed_confirm_delete).replace("{title}", title)); // message to display
    	builder.setPositiveButton(R.string.accept, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//delete feed
						FeedsModel.delete(feedId);
						
						//refresh list
						refreshFeedsList();
		    	    					    	
				    	//Toast
				    	String msg = getString(R.string.feed_deleted).replace("{title}", title);
				    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();				    	
					}
				}
    	);
    	builder.setNegativeButton(R.string.cancel, null);  
    	builder.show();
    }
}
