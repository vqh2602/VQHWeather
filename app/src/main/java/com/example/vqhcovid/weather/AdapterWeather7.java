package com.example.vqhcovid.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vqhcovid.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterWeather7 extends ArrayAdapter<Weather7> {

    public AdapterWeather7(Context context, int resource, ArrayList<Weather7> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_weather7, null);
        }

        Weather7 p = getItem(position);
        if (p != null) {
            TextView textView_date = (TextView) view.findViewById(R.id.textView_date);
            TextView textView_weather = (TextView) view.findViewById(R.id.textView_weather);
            TextView textView_min= (TextView) view.findViewById(R.id.textView_min);
            TextView textView_max= (TextView) view.findViewById(R.id.textView_max);
            TextView textView_sunset= (TextView) view.findViewById(R.id.textView_sunset);
            TextView textView_sunrise= (TextView) view.findViewById(R.id.textView_sunrise);
            ImageView image_weather = (ImageView) view.findViewById(R.id.image_weather);

            textView_date.setText(p.date);
            textView_weather.setText(p.troi);
            textView_min.setText(p.min);
            textView_max.setText(p.max);
            textView_sunset.setText(p.sunset);
            textView_sunrise.setText(p.sunrise);
            image_weather.setImageResource(p.imageUrl);
        }

        return view;
    }
}
