package com.rc.nowtv.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rc.nowtv.R;
import com.rc.nowtv.models.ChatMessage;
import com.rc.nowtv.models.User;
import com.rc.nowtv.utils.LocalStorage;

import java.io.IOException;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import tv.icomp.vod.vodplayer.VodPlayer;
import tv.icomp.vod.vodplayer.trackselector.evaluator.source.Evaluator;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    private LocalStorage localStorage;
    private VodPlayer vodPlayer;
    private User user;

    private View rootView;
    private SimpleExoPlayerView exoPlayerView;
    private ListView listOfMessage;
    private RelativeLayout layoutShowChatLive;
    private RelativeLayout layoutChat;
    private ImageView imgShowChat;

    private EmojiconEditText emojiconEditText;
    private ImageView emojiButton,submitButton;
    private EmojIconActions emojIconActions;

    private FirebaseListAdapter<ChatMessage> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initView();
        initListener();
        initVodPlayer();
        initChat();
    }

    private void initView() {
        rootView = findViewById(android.R.id.content);

        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.sv_exo_player);

        layoutShowChatLive = (RelativeLayout) findViewById(R.id.layout_show_chat_live);
        imgShowChat = (ImageView) findViewById(R.id.iv_show_chat);
        layoutChat = (RelativeLayout) findViewById(R.id.layout_chat);
        listOfMessage = (ListView) findViewById(R.id.list_of_message);

        emojiButton = (ImageView)findViewById(R.id.emoji_button);
        submitButton = (ImageView)findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText)findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(),rootView,emojiButton,emojiconEditText);
        emojIconActions.ShowEmojicon();

        localStorage = LocalStorage.getInstance(getApplicationContext());
        user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

        if(user == null) {
            submitButton.setEnabled(false);
            emojiconEditText.setEnabled(false);

            emojiconEditText.setText(R.string.warning_interactions);
            emojIconActions.closeEmojicon();
        } else {
            Toast.makeText(rootView.getContext(), rootView.getContext().getString(R.string.welcome)
                    + ", " + user.getName(), Toast.LENGTH_SHORT).show();
            submitButton.setEnabled(true);
            emojiconEditText.setEnabled(true);
            emojiconEditText.setText("");
            emojIconActions.ShowEmojicon();
        }
    }

    private void initListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emojiconEditText.getText().toString().length() > 0) {

                    FirebaseDatabase.getInstance().getReference().push().setValue(
                            new ChatMessage(emojiconEditText.getText().toString(), user.getName(), user.getUrlPhoto()));

                    emojiconEditText.setText("");
                    emojiconEditText.requestFocus();
                }
            }
        });

        layoutShowChatLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutChat.getVisibility() == View.GONE) {
                    showChat(true);
                } else {
                    showChat(false);
                }
            }
        });
    }

    private void initChat() {
        displayChatMessage();
    }

    private void initVodPlayer() {
        String url = com.rc.nowtv.utils.C.URL_LIVE_VIDEO;
        vodPlayer = new VodPlayer(getApplicationContext(), exoPlayerView);
        vodPlayer.setVodPlayerListener(new VodPlayer.VodPlayerListener() {
            @Override
            public void onPrepared() {
                vodPlayer.play();
            }

            @Override
            public void onLoadingError(IOException e) {

            }

            @Override
            public void onChangeLoaderState(int i) {

            }

            @Override
            public long getSeekTo() {
                return 0;
            }
        });

        Evaluator evaluator = new Evaluator() {
            @Override
            public int getSelectedTrack(long l, MediaChunk mediaChunk, TrackGroup trackGroup, int i, BandwidthMeter bandwidthMeter) {
                return 0;
            }
        };
        vodPlayer.buildPlayer(url, C.TYPE_HLS, evaluator);
    }

    private void displayChatMessage() {
        Log.d(TAG, FirebaseDatabase.getInstance().getReference()  != null ?
                FirebaseDatabase.getInstance().getReference().toString() : "FirebaseDatabase é nulo!");

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView messageText, messageUser, messageTime;
                final ImageView photoUser;

                photoUser = (ImageView) v.findViewById(R.id.photo_user);
                messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                Glide
                        .with(getApplicationContext())
                        .load(model.getUrlUserPhoto())
                        .asBitmap().centerCrop()
                        .placeholder(R.mipmap.user)
                        .into(new BitmapImageViewTarget(photoUser) {

                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        photoUser.setImageDrawable(circularBitmapDrawable);
                    }
                });

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getUsername());
                messageTime.setText(DateFormat.format("HH:mm", model.getTime()));

                scrollToLast();
            }
        };
        listOfMessage.setAdapter(adapter);
    }

    private void showChat(boolean isShow) {
        if (isShow) {
            setVisibilityChatItems(View.VISIBLE);
            imgShowChat.setImageResource(R.mipmap.ic_expand_more_black_24dp);
        } else {
            setVisibilityChatItems(View.GONE);
            imgShowChat.setImageResource(R.mipmap.ic_expand_less_black_24dp);
        }
    }

    private void setVisibilityChatItems(int visibility) {
        layoutChat.setVisibility(visibility);
    }

    private void cleanDBFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.removeValue();
    }

    public void scrollToLast() {
        listOfMessage.clearFocus();
        listOfMessage.setSelection(adapter.getCount()-1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (vodPlayer != null && vodPlayer.getExoPlayer() != null) vodPlayer.getExoPlayer().release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vodPlayer != null && vodPlayer.getExoPlayer() != null) vodPlayer.getExoPlayer().release();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
