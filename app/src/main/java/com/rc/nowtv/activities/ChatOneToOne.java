package com.rc.nowtv.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rc.nowtv.R;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatOneToOne extends Activity {

    private ImageButton btnBack;
    private ImageView icUser;
    private TextView tvUsername;
    private TextView tvStatus;

    private ListView lvChat;
    private ImageView icEmojin;
    private ImageView btnSend;
    private EmojiconEditText etChatText;
    private EmojIconActions emojIconActions;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_one_to_one);

        initView();
    }

    private void initView() {
        rootView = findViewById(android.R.id.content);

        btnBack = (ImageButton) findViewById(R.id.ic_back);
        icUser = (ImageView) findViewById(R.id.ic_user_one_to_one);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvStatus = (TextView) findViewById(R.id.tv_status);

        lvChat = (ListView) findViewById(R.id.lv_chat_one_to_one);
        icEmojin = (ImageView) findViewById(R.id.ic_emoji_button);
        btnSend = (ImageView) findViewById(R.id.btn_send);
        etChatText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(), rootView, icEmojin, etChatText);
        emojIconActions.ShowEmojicon();
    }

    public void onBack(View view) {
        finish();
    }
}
