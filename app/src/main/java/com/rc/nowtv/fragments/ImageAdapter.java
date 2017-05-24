package com.rc.nowtv.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.rc.nowtv.R;

/**
 * Created by camila on 20/05/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 180));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setBackgroundResource(R.color.colorWhite);
            imageView.setPadding(60, 60, 60, 60);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide
                .with(mContext)
                .load("")
                .centerCrop()
                .placeholder(mThumbIds[position])
                .into(imageView);

        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.computer, R.drawable.game,
            R.drawable.food, R.drawable.fitness,
            R.drawable.music, R.drawable.pet,

    };
}
