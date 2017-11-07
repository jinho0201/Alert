package com.management.location.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.management.location.R;
import com.management.location.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {


    private Context mContext;
    List<item> mItems = new ArrayList<item>();
    private LayoutInflater inflater;





    public ListViewAdapter(Context context, List<item> items) {

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
            convertview = this.inflater.inflate(R.layout.item_layout, null);

        TextView name = (TextView) convertview.findViewById(R.id.name);
        TextView bell = (TextView) convertview.findViewById(R.id.lat);
        TextView wifi = (TextView) convertview.findViewById(R.id.wifi);



        name.setText(mItems.get(i).getName());
        bell.setText(mItems.get(i).getBell());
        wifi.setText(mItems.get(i).getWifi());







        return convertview;
    }
}
