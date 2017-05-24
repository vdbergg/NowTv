package com.rc.nowtv.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rc.nowtv.R;
import com.rc.nowtv.models.ChatMessage;

import java.util.ArrayList;

/**
 * Created by berg on 23/05/17.
 */

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private Context context;
    private ArrayList<ChatMessage> list;

    public ChatAdapter(Context context, int resource, ArrayList<ChatMessage> list) {
        super(context, resource);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            view.setTag(holder);

            holder.text = (TextView) view.findViewById(R.id.message_text);
            holder.username = (TextView) view.findViewById(R.id.message_user);
            holder.time = (TextView) view.findViewById(R.id.message_time);
            holder.photo = (ImageView) view.findViewById(R.id.photo_user);

        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        if (list.size() > 0) {
            ChatMessage message = list.get(position);
            String text = "" + message.getMessageText();
            String username = "" + message.getUsername();
            String photo = message.getUrlUserPhoto();

            holder.text.setText(text);
            holder.username.setText(username);
            holder.time.setText(DateFormat.format("HH:mm", message.getTime()));

//            Glide
//                    .with(context)
//                    .load(photo)
//                    .centerCrop()
//                    .placeholder(R.id.photo_user)
//                    .crossFade()
//                    .into(holder.photo);
        }


        return view;
    }

    private class ViewHolder {
        public TextView text;
        public TextView username;
        public TextView time;
        public ImageView photo;
    }
}
