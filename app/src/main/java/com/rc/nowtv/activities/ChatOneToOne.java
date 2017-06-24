package com.rc.nowtv.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rc.nowtv.R;
import com.rc.nowtv.adapters.ChatOneToOneAdapter;
import com.rc.nowtv.models.ChatMessage;
import com.rc.nowtv.models.User;
import com.rc.nowtv.models.Video;
import com.rc.nowtv.utils.C;
import com.rc.nowtv.utils.LocalStorage;
import com.rc.nowtv.xmpp.MyXMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatOneToOne extends Activity {

    private static final String TAG = ChatOneToOne.class.getSimpleName();

    private ImageButton btnBack;
    private ImageView icUser;
    private TextView tvUsername;
    private TextView tvStatus;

    private ListView lvChat;
    private ImageView icEmojin;
    private ImageView btnSend;
    private EmojiconEditText etChatText;
    private EmojIconActions emojIconActions;
    private MyXMPP myXMPP;

    private ChatOneToOneAdapter chatOneToOneAdapter;
    private ArrayList<ChatMessage> msgs = new ArrayList<>();
    private LocalStorage localStorage;
    private User user;
    private Video video;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_one_to_one);

        initView();
        initListener();
        initXMPP();
        initValue();
    }

    private void initXMPP() {
        myXMPP = MyXMPP.getInstance(getApplicationContext(), C.DOMAIN, C.URL_SERVER, user.getUsername(),
                user.getIdUser(), video, new MyXMPP.ReceivedMessages() {
                    @Override
                    public void onReceived(ChatMessage chatMessage) {

                    }

                    @Override
                    public void onReceived(Chat chat, final Message message) {

                    }

                    @Override
                    public void onConnected() {

                    }
                });

        myXMPP.setOnReceived(new MyXMPP.ReceivedMessages() {
            @Override
            public void onReceived(ChatMessage chatMessage) {

            }

            @Override
            public void onReceived(Chat chat, final Message message) {
                if (message.getType() == Message.Type.chat && message.getBody() != null) {

                    final ChatMessage chatMessage = new ChatMessage(message.getBody(), ChatMessage.MSG_RECEIVED);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processMessage(chatMessage);
                            Log.d(TAG, "Message received: " + message.getBody());
                        }
                    });
                }
            }

            @Override
            public void onConnected() {
//                if (getIntent().getExtras().getBoolean("creating")) {
//                    String jId = getIntent().getExtras().getString("jId");
//                    myXMPP.getMuc().createPrivateChat(jId, new ChatMessageListener() {
//                        @Override
//                        public void processMessage(Chat chat, Message message) {
//                            myXMPP.setChat(chat);
//                            Log.d(TAG, "Message received: " + message.getBody());
//                        }
//                    });
//                }
            }
        });
    }

    private void initValue() {
        msgs = new ArrayList<>();
        chatOneToOneAdapter = new ChatOneToOneAdapter(getApplicationContext(), 0, msgs);
        lvChat.setAdapter(chatOneToOneAdapter);
        lvChat.setSelection(chatOneToOneAdapter.getCount() -1);
    }

    private void initView() {
        rootView = findViewById(android.R.id.content);
        localStorage = LocalStorage.getInstance(getApplicationContext());
        video = localStorage.getObjectFromStorage(LocalStorage.OBJ_VIDEO, Video.class);
        user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

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

    private void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etChatText.getText().toString().length() > 0) {

                    sendMessage(myXMPP.getChat(), etChatText.getText().toString());

                    etChatText.setText("");
                    etChatText.requestFocus();
                }
            }
        });
    }

    public void onBack(View view) {
        finish();
    }

    public void sendMessage(Chat chat, String msg) {
        try {
            chat.sendMessage(msg);

            chatOneToOneAdapter.add(new ChatMessage(msg, ChatMessage.MSG_SEND));
            chatOneToOneAdapter.notifyDataSetChanged();
            lvChat.setSelection(chatOneToOneAdapter.getCount() -1);

            Log.d(TAG, "Msg enviada: " + msg);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(final ChatMessage chatMessage) {
        msgs.add(chatMessage);
//        chatOneToOneAdapter.add(chatMessage);
        chatOneToOneAdapter.notifyDataSetChanged();
        lvChat.setSelection(chatOneToOneAdapter.getCount() -1);
    }
}
