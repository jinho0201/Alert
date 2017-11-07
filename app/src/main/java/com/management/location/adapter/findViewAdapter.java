package com.management.location.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.management.location.R;

import java.util.ArrayList;


public class findViewAdapter extends BaseAdapter {


    private Context mContext;
    ArrayList<findItem> mItems = new ArrayList<findItem>();
    private LayoutInflater inflater;





    public findViewAdapter(Context context,  ArrayList<findItem> items) {

        this.mContext = context;
        this.mItems = items;

    }


    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {


        if (inflater == null)
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertview == null)
            convertview = this.inflater.inflate(R.layout.find_item_layout, null);

        TextView name = (TextView) convertview.findViewById(R.id.name);
        TextView lat = (TextView) convertview.findViewById(R.id.lat);
        TextView log = (TextView) convertview.findViewById(R.id.log);


        name.setText(mItems.get(i).getName());
        lat.setText(mItems.get(i).getLat());
        log.setText(mItems.get(i).getLon());







        return convertview;
    }
}
