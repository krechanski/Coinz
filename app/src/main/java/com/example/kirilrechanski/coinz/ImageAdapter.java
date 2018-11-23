package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class ImageAdapter extends BaseAdapter {
    private Context context;
    // references to our images
    private Integer[] coinCurrencies = {
            R.drawable.blue_marker, R.drawable.green_marker,
            R.drawable.red_marker, R.drawable.yellow_marker
    };

    public ImageAdapter(Context con) {
        this.context = con;

    }

    public int getCount() {
        return coinCurrencies.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(coinCurrencies[position]);
        return imageView;
    }


}
