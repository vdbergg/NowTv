package com.rc.nowtv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rc.nowtv.R;
import com.rc.nowtv.models.ChatMessage;

import java.util.ArrayList;

/**
 * Created by berg on 29/05/17.
 */

public class ChatOneToOneAdapter extends ArrayAdapter<ChatMessage> {
    private ArrayList<ChatMessage> itens;
    private Context context;

    public ChatOneToOneAdapter(Context context, int resource, ArrayList<ChatMessage> chatMessages) {
        super(context, resource, chatMessages);
        this.context = context;
        this.itens = chatMessages;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_one_to_one, null);
            holder = new ViewHolder();
            view.setTag(holder);

            holder.textSend = (TextView) view.findViewById(R.id.tv_msg_right);
            holder.textReceived = (TextView) view.findViewById(R.id.tv_msg_left);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        if (itens.size() > 0) {
            ChatMessage message = itens.get(position);
            String text = "" + message.getMessageText();

            if (message.getType() == ChatMessage.MSG_RECEIVED) {
                holder.textSend.setVisibility(View.GONE);
                holder.textReceived.setText(text);
                holder.textReceived.setVisibility(View.VISIBLE);
            } else {
                holder.textReceived.setVisibility(View.GONE);
                holder.textSend.setText(text);
                holder.textSend.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private class ViewHolder {
        public TextView textSend;
        public TextView textReceived;
    }
}
