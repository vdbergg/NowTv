package com.rc.nowtv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rc.nowtv.R;
import com.rc.nowtv.models.Video;

import java.util.ArrayList;

/**
 * Created by berg on 24/05/17.
 */

public class VideoListAdapter extends ArrayAdapter<Video> {

    private ArrayList<Video> listVideos;
    private Context context;

    public VideoListAdapter(Context context, ArrayList<Video> listVideos) {
        super(context, 0, listVideos);
        this.context = context;
        this.listVideos = listVideos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_video, null);
            holder = new ViewHolder();
            convertView.setTag(holder);

            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.duration = (TextView) convertView.findViewById(R.id.tv_duration);
            holder.picture = (ImageView) convertView.findViewById(R.id.ic_video);
//            holder.published = (TextView) convertView.findViewById(R.id.tv_published);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Video video = listVideos.get(position);

        holder.title.setText(video.getTitle());
        holder.picture.setImageResource(video.getPicture());

        return convertView;
    }

    public class ViewHolder {
        private TextView title;
        private TextView duration;
        private TextView published;
        private ImageView picture;
    }
}
