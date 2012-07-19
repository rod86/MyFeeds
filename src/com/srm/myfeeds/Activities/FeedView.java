package com.srm.myfeeds.Activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.srm.myfeeds.R;
import com.srm.myfeeds.Models.FeedsModel;
import com.srm.myfeeds.Classes.Entry;
import com.srm.myfeeds.Classes.FeedParser;
import com.srm.myfeeds.Adapters.EntriesAdapter;


public class FeedView extends Activity{	
	
	private int feedId;					//selected feed ID
	HashMap<String, String> feed;  		//current feed info (title, url, ...)
	protected ListView entriesList;		//Listview that contains list of entries
	private readFeedTask readFeedTask; 	//task that reads feeds
	private EntriesAdapter adapter;		//listview's adapter
	private int itemsPerPage = 7;		//How many entries will be loaded by page
	private int total = 0;				//How many items has the listview
	private View footerView;			//View "Loading more ..." that is appended at the end of listview
		
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main); 
        
        //pick up values from feeds list
        Bundle extras = getIntent().getExtras();
        feedId = extras.getInt("feedId");      
        
        //get current feed info
        FeedsModel FeedsModel = new FeedsModel(this);
        feed = FeedsModel.getFeed(feedId);    
        
        //init vars
        entriesList = (ListView)findViewById(R.id.list);
        entriesList.setOnScrollListener(entriesScrollListener);
        entriesList.setOnItemClickListener(entryListener);        
                
        //Set empty view that is displayed when listview hasn't items			
    	TextView emptyView = (TextView)findViewById(R.id.listEmpty);
    	emptyView.setText(getString(R.string.loading));
		entriesList.setEmptyView(emptyView);
        
		//add loading view to bottom
        footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loadmore, null, false);
        entriesList.addFooterView(footerView, null, false);
		
        adapter = new EntriesAdapter(getApplicationContext(), new ArrayList<Entry>());
		entriesList.setAdapter(adapter);		  
				
        loadEntries(0);     
    }    
  
    
    public void loadEntries(int startItem){
    	//Init & start task
        readFeedTask = new readFeedTask();               
        readFeedTask.execute(startItem);        
    }
    
    //event listener that triggers when user touch listview's item
    OnItemClickListener entryListener = new OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long id) {    		
    		
    		//Open selected item's url in Android browser
    		TextView link = (TextView)v.findViewById(R.id.link);    		
    		Uri uri = Uri.parse((String)link.getText());
    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    		startActivity(intent);    		
		}
    }; // end listener
    
    
    @Override
    public void onStop(){
    	super.onStop();
    	cancelReadFeedTask();
    }
    
    /**
     * Cancels current feed read task if is active
     */
    public void cancelReadFeedTask(){
    	
    	if (readFeedTask != null && readFeedTask.getStatus() != AsyncTask.Status.FINISHED ){
    		readFeedTask.cancel(true);
    	}        	
    }
    
    
    //Scroll listener for the listview  
    OnScrollListener entriesScrollListener = new OnScrollListener() {		
    	
    	private int visibleThreshold = 5;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;
    	
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}		
				
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (loading) {
	            if (totalItemCount > previousTotal) {
	                loading = false;
	                previousTotal = totalItemCount;
	                currentPage++;
	            }
	        }
	        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
	        	loadEntries(itemsPerPage*currentPage);       	
	        	loading = true;        	
	        }		
		}
	};   
        
    
    //Task that reads feeds and put them into a listview
    public class readFeedTask extends AsyncTask<Integer, Void, ArrayList<Entry>>{   	
    	    	
    	private FeedParser parser;   	    	
    	    	
    	@Override
    	protected void onPreExecute(){}
    	
    	@Override
		protected ArrayList<Entry> doInBackground(Integer... startItem) {		    		
			parser = new FeedParser(feed.get("url"));
    		parser.setItemsPerPage(itemsPerPage);
			return parser.read(startItem[0]);		    		
		}
		
    	@Override
		protected void onPostExecute(ArrayList<Entry> result){	    		   		  		    		
			
    		String message = null;
    		
    		//If there's error, set error message as emptyview for listview.
    		//if not, set "no rows" message
			if (result == null){				
				message = getString(R.string.feed_error_read);				
			}else{				
								
				if (result!=null && result.size() > 0){
					
					total += result.size();
					
					for (int i=0;i<result.size();i++){						
						adapter.add(result.get(i));
					}
					
					adapter.notifyDataSetChanged();
				}					
				
				message = getString(R.string.feed_no_entries);
			}
			
			//Set empty view that is displayed when listview hasn't items			
	    	TextView emptyView = (TextView)findViewById(R.id.listEmpty);
	    	emptyView.setText(message);
			entriesList.setEmptyView(emptyView);
			
			//if we arrived at the end of listview, remove footer
			if (total >= parser.count()){			
				entriesList.removeFooterView(footerView);
			}
		}
    	
    	@Override
    	protected void onCancelled() {
    		if (parser != null) parser.stop();
    	}    	
    	
    	
    }    
 
}
