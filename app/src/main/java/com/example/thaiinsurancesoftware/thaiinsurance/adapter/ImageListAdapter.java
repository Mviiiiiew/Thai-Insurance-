package com.example.thaiinsurancesoftware.thaiinsurance.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thaiinsurancesoftware.thaiinsurance.R;
import com.example.thaiinsurancesoftware.thaiinsurance.model.ImageList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wasabi on 4/25/2017.
 */

public class ImageListAdapter extends BaseAdapter {
    private static Activity activity;
    private static LayoutInflater inflater;
  private  ArrayList<ImageList> imageLists;

    public ImageListAdapter(Activity activity, ArrayList<ImageList>imageLists ) {
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLists = imageLists;
    }

    @Override
    public int getCount() {
        return imageLists.size();
    }

    @Override
    public Object getItem(int i) {
        return imageLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        v = inflater.inflate(R.layout.list_item_image,null);
        TextView txt_image_name = (TextView)v.findViewById(R.id.txt_image_name);
        ImageView imgView =(ImageView)v.findViewById(R.id.imgView);

        txt_image_name.setText(imageLists.get(i).getName());
        Glide.with(activity).load(imageLists.get(i).getUri()).into(imgView);

        return v;
    }
}
