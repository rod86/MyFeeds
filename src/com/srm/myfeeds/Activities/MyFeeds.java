package com.srm.myfeeds.Activities;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

import com.srm.myfeeds.R;
import com.srm.myfeeds.Models.CategoriesModel;
import com.srm.myfeeds.Models.FeedsModel;
import com.srm.myfeeds.Adapters.CategoriesAdapter;


public class MyFeeds extends Activity {	
	
	protected CategoriesModel CategoriesModel;				//categories model object
	protected ListView categoriesList;					//ListView that contains categories list
	protected View dialogLayout;						//dialog view used in add/edit category	
	protected int categoryId;						//category id used in edit
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);            
        
        //init categories ListView and attach events
        categoriesList = (ListView)findViewById(R.id.list);
        categoriesList.setOnItemClickListener(categoryListener);
        
        //init categories Model
        CategoriesModel = new CategoriesModel(this);        
        
        loadCategories();      
        
        //associate contextual menu
        registerForContextMenu(categoriesList);
    }
    
    
    //Loads categories and puts them into ListView
    public void loadCategories(){    	
        CategoriesAdapter adapter = new CategoriesAdapter(this, CategoriesModel.getCategories());
        categoriesList.setAdapter(adapter); 
        
      //Set empty view that is displayed when listview hasn't items
    	TextView emptyView = (TextView)findViewById(R.id.listEmpty);
    	emptyView.setText(getString(R.string.categories_list_empty));
    	categoriesList.setEmptyView(emptyView);	
    }
        
    
    //event listener that triggers when user touch listview's item
    OnItemClickListener categoryListener = new OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long id) {    		
    		
    		//get category id of selected category
    		int categoryId = CategoriesModel.findByName((String)((TextView)v.findViewById(R.id.name)).getText());
    		    	    		
    		//Launch activity that lists feeds of the selected category
    		Intent feedList = new Intent(MyFeeds.this, Feeds.class);    	
			feedList.putExtra("categoryId", categoryId);
			startActivity(feedList);
    		
		}
    }; // end listener
    
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
    			dialogCategory();   			
    			return true;    		
    	}
    	
    	return false;
    }
    
    //contextual menu for each listview item
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
       
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;     
        menu.setHeaderTitle(((TextView)info.targetView.findViewById(R.id.name)).getText());             
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ctx, menu);
    }
    
    //on select any option of menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
     
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        
        String name = (String)((TextView)info.targetView.findViewById(R.id.name)).getText();
    	int id = CategoriesModel.findByName(name);
    	
        switch (item.getItemId()) {
            case R.id.edit:
            	dialogCategory(id);
                return true;
                
            case R.id.delete:
            	deleteCategory(id);           	
                return true;
                
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    public void deleteCategory(final int categoryId){
    	
    	final String name = CategoriesModel.getCategoryName(categoryId);    	
    
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.delete); // title bar string
        builder.setMessage(getString(R.string.category_confirm_delete).replace("{name}", name)); // message to display
    	builder.setPositiveButton(R.string.accept, 
    			new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						//remove the category's feeds
						FeedsModel FeedsModel = new FeedsModel(getApplicationContext());
						FeedsModel.deleteByCategory(categoryId);
						
						//remove feed
						CategoriesModel.delete(categoryId);						
						
						//refresh list
		    	    	loadCategories(); 
				    	
				    	//Toast
				    	String msg = getString(R.string.category_deleted).replace("{name}", name);
				    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();    
					}
				}
    	);
    	builder.setNegativeButton(R.string.cancel, null);  
    	builder.show();
    }
    
    //dialog displayed on editing/adding category    
    public void dialogCategory(){
    	dialogCategory(0);
    }
    
    public void dialogCategory(int id){
    	
    	 AlertDialog.Builder form = new AlertDialog.Builder(this);    	 
        	 
    	 LayoutInflater inflater = getLayoutInflater();
    	 dialogLayout = inflater.inflate(R.layout.category_popup, null);
    	 form.setView(dialogLayout);   	
    	 form.setNegativeButton(getString(R.string.cancel), null);
    	 
    	//if is edit mode
    	if (id!=0){
    		 
    		String name = CategoriesModel.getCategoryName(id);
    		 
    		form.setTitle(getString(R.string.editCategory));
    		form.setPositiveButton(getString(R.string.accept), editCategoryListener); 
    		
	 		EditText category = (EditText)dialogLayout.findViewById(R.id.category);
	 		category.setText(name);
	 		
	 		this.categoryId = id;
	 		
    	 }else{
    		 form.setTitle(getString(R.string.addCategory));
    		 form.setPositiveButton(getString(R.string.accept), addCategoryListener); 
    	 }   	 
    	 
    	 AlertDialog formDialog = form.create();
    	 formDialog.show();		
    }  
    
    
    //OnClickListener on adding category
    OnClickListener addCategoryListener = new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText category = (EditText)dialogLayout.findViewById(R.id.category);
	    	String name = category.getText().toString().trim();
	    	
	    	//check if is empty    	    	
	    	if (category.getText().toString().equals("")){    	    		
	    		Toast.makeText(getApplicationContext(), R.string.category_error_empty, Toast.LENGTH_SHORT).show();    	    		
	    		return;
	    	}
	    	
	    	//check if exists in db
	    	Integer exists = CategoriesModel.findByName(name);
	    	    	
	    	//if not exists, add
	    	if (exists == null){
	    	
    	    	//add to db
    	    	CategoriesModel.add(name);	  
    	    	
    	    	String msg = getString(R.string.category_added).replace("{name}", name);
    	    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    	    	
    	    	//refresh list
    	    	loadCategories(); 
	    	}else{
	    		String msg = getString(R.string.category_exists).replace("{name}", name);
	    		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	    	}			
		}    	
    };
    

    //OnCLickListener on editing category
    OnClickListener editCategoryListener = new OnClickListener() {		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			EditText category = (EditText)dialogLayout.findViewById(R.id.category);
	    	String name = category.getText().toString().trim();
	    	
	    	//check if is empty    	    	
	    	if (category.getText().toString().equals("")){    	    		
	    		Toast.makeText(getApplicationContext(), R.string.category_error_empty, Toast.LENGTH_SHORT).show();    	    		
	    		return;
	    	}
	    	
	    	//check if exists in db    	
	    	boolean isUnique = CategoriesModel.isUniqueName(name, categoryId);    	    	
	    	
	    	//if not exists, update
	    	if (isUnique){    	    	
    	    	//add to db
    	    	CategoriesModel.update(categoryId, name);	  
    	    	
    	    	String msg = getString(R.string.category_updated).replace("{name}", name);
    	    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    	    	
    	    	//refresh list
    	    	loadCategories(); 
	    	}else{
	    		String msg = getString(R.string.category_exists).replace("{name}", name);
	    		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	    	}			
		}
	};
}