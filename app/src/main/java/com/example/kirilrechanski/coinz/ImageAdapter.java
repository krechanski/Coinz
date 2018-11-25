package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

class ImageAdapter extends BaseAdapter {
    String [] currencies;
    Context context;
    int [] markers;
    private static LayoutInflater inflater=null;
    public ImageAdapter(Wallet wallet, String[] coinCurrencies, int[] coinMarkers) {
        // TODO Auto-generated constructor stub
        currencies=coinCurrencies;
        context=wallet;
        markers=coinMarkers;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return currencies.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView coinText;
        ImageView coinMarker;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.coin_layout, null);
        holder.coinText =(TextView) rowView.findViewById(R.id.coinCurrency);
        holder.coinMarker =(ImageView) rowView.findViewById(R.id.coinMarker);

        holder.coinText.setText(currencies[position]);
        holder.coinMarker.setImageResource(markers[position]);

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+currencies[position], Toast.LENGTH_SHORT).show();
            }
        });

        return rowView;
    }


}
