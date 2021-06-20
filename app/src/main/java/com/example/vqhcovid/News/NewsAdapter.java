package com.example.vqhcovid.News;

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

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, int resource, ArrayList<News> items) {
        super(context, resource, items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_news, null);
        }

        News p = getItem(position);
        if (p != null) {
            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            ImageView imgView = (ImageView) view.findViewById(R.id.imageView);

            tvTitle.setText(p.title);
            Picasso.with(getContext()).load(p.imageUrl).into(imgView);
        }

        return view;
    }
}
