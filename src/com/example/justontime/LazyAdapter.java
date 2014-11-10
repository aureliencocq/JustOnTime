package com.example.justontime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
	 
    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater=null;
 
    public LazyAdapter(Activity a, ArrayList<String> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return data.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView route = (TextView)vi.findViewById(R.id.route); // title
        TextView trainType = (TextView)vi.findViewById(R.id.train_type); // artist name        

        String fullItem = data.get(position);
 
        // Setting all values in listview
        route.setText(fullItem);
        trainType.setText("train TER");
        
        return vi;
    }
}