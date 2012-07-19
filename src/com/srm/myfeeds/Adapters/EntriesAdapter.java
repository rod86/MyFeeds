package com.srm.myfeeds.Adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.srm.myfeeds.R;
import com.srm.myfeeds.Classes.Entry;

public class EntriesAdapter extends ArrayAdapter<Entry> {
	
	private final Context context;								//Application context
	private final ArrayList<Entry> items;		//Array that contains rows
	private final static int VIEW = R.layout.entry_item;			//Row view id

	public EntriesAdapter(Context context, ArrayList<Entry> items) {
		super(context, VIEW, items);
		this.context = context;
		this.items = items;
	}
	
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		
		//get row layout
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(VIEW, parent,false);
		
		Entry item = items.get(index);	
		
		//assign values
		TextView title = (TextView)rowView.findViewById(R.id.title);
		title.setText(item.getTitle());	
		
		TextView date = (TextView)rowView.findViewById(R.id.date);
		date.setText(item.getDate());		
		
		TextView link = (TextView)rowView.findViewById(R.id.link);
		link.setText(item.getUrl());
		
		ImageView thumbnail = (ImageView)rowView.findViewById(R.id.thumbnail);								
		thumbnail.setImageDrawable(item.getDrawable());		
		thumbnail.setContentDescription(item.getTitle());
			
		return rowView;
	}	
	
}
