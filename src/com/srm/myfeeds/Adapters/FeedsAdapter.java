package com.srm.myfeeds.Adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.srm.myfeeds.R;

public class FeedsAdapter extends ArrayAdapter<HashMap<String,String>> {
	
	private final Context context;								//Application context
	private final ArrayList<HashMap<String,String>> items;		//Array that contains rows
	private final static int VIEW = R.layout.feed_item;			//Row view id

	public FeedsAdapter(Context context, ArrayList<HashMap<String,String>> items) {
		super(context, VIEW, items);
		this.context = context;
		this.items = items;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		
		//get row layout
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(VIEW, parent,false);
		
		HashMap<String, String> item = items.get(index);	
		
		//assign values
		TextView title = (TextView)rowView.findViewById(R.id.title);
		title.setText(item.get("title"));	
		
		TextView url = (TextView)rowView.findViewById(R.id.url);
		url.setText(item.get("url"));
			
		return rowView;
	}
	
	
}