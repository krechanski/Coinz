package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Coin> coins;
    public List selectedPositions = new ArrayList();

    // 1
    public ImageAdapter(Context context, List<Coin> coins) {
        this.mContext = context;
        this.coins = coins;
    }

    // 2
    @Override
    public int getCount() {
        return coins.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Coin coin = coins.get(position);



        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.coin_layout, null);
        }

        // 3
        final ImageView coinMarker = (ImageView) convertView.findViewById(R.id.coinMarker);
        final TextView coinCurrency = (TextView) convertView.findViewById(R.id.coinCurrency);
        final TextView coinValue = (TextView) convertView.findViewById(R.id.coinValue);

        // 4
        coinMarker.setImageResource(coin.getIcon());
        coinCurrency.setText(coin.getCurrency());
        String stringDouble = Double.toString(coin.getValue());
        coinValue.setText(stringDouble);

        if (selectedPositions.contains(position)) {
            convertView.setBackgroundResource(R.drawable.coin_selected);
        }

        else {
            convertView.setBackgroundResource(R.drawable.coin_notselected);
        }


        return convertView;

    }


}
