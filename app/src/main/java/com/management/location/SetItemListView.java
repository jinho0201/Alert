package com.management.location;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by L on 2015-10-20.
 */
public class SetItemListView extends RelativeLayout{

    public SetItemListView(Context context) {
        super(context);
        init(context);
    }


    public void init(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.set_item_list_view, this);


    }


    public void setTitle(String text){

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(text);
        title.setTypeface(Typeface.MONOSPACE);

    }

    public void setSubTitle(String text){
        TextView sub = (TextView) findViewById(R.id.sub);
        sub.setText(text);
        sub.setTypeface(Typeface.MONOSPACE);

    }

    public void setIcon(Drawable img){

        ImageView icon = (ImageView) findViewById(R.id.icon);
        icon.setImageDrawable(img);

    }

}
