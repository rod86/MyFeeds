package com.srm.myfeeds.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.srm.myfeeds.R;

public class CategoriesAdapter extends ArrayAdapter<String> {
	
	private final Context context;				//Application context
	private final String[] values;				//Array that contains rows		
	private final static int VIEW = R.layout.category_item;			//Row view id
	
	public CategoriesAdapter(Context context, String[] values){
		super(context, VIEW, values);
		this.context = context;
		this.values = values;
	}
	
	//view for each item
	@Override
	public View getView (int index, View view, ViewGroup parent){
		
		//get row layout
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(VIEW, parent,false);
		
		//assign values
		TextView text = (TextView)rowView.findViewById(R.id.name);
		text.setText(values[index]);			
			
		return rowView;
	}	
}
