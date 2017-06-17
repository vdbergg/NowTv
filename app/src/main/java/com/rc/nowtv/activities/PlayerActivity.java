package com.rc.nowtv.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.rc.nowtv.R;
import com.rc.nowtv.adapters.ChatAdapter;
import com.rc.nowtv.adapters.ListDrawerAdapter;
import com.rc.nowtv.models.ChatMessage;
import com.rc.nowtv.models.Member;
import com.rc.nowtv.models.User;
import com.rc.nowtv.utils.LocalStorage;
import com.rc.nowtv.utils.UtilDesign;
import com.rc.nowtv.xmpp.MyXMPP;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

import java.io.IOException;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
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
    private ImageButton btnGroupChat;
    private ImageView btnAlertChat;

    private EmojiconEditText emojiconEditText;
    private ImageView emojiButton, submitButton;
    private EmojIconActions emojIconActions;

    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> listMessages;

    private RelativeLayout leftRL;
    private DrawerLayout drawerLayout;
    private ListView lvMembersGroup;
    private ListDrawerAdapter listDrawerAdapter;
    private ArrayList<Member> itens;
    private boolean isShowDrawer;

    private MyXMPP myXMPP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        showActionBar();
        initView();
        initListener();
        initVodPlayer();
        initXMPPServer();
    }

    private void initDrawer() {
        itens = new ArrayList<>();
        itens = myXMPP.getListMembers();

        listDrawerAdapter = new ListDrawerAdapter(this, itens);
        lvMembersGroup.setAdapter(listDrawerAdapter);
        UtilDesign.setListViewHeightBasedOnChildren(lvMembersGroup);
    }

    private void showActionBar() {
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.menu_custom, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled (false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    private void initView() {
        rootView = findViewById(android.R.id.content);

        exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.sv_exo_player);

        layoutShowChatLive = (RelativeLayout) findViewById(R.id.layout_show_chat_live);
        imgShowChat = (ImageView) findViewById(R.id.iv_show_chat);
        layoutChat = (RelativeLayout) findViewById(R.id.layout_chat);
        listOfMessage = (ListView) findViewById(R.id.list_of_message);

        leftRL = (RelativeLayout)findViewById(R.id.whatYouWantInLeftDrawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        lvMembersGroup = (ListView) findViewById(R.id.lv_members);
        btnGroupChat = (ImageButton) findViewById(R.id.btn_group_chat);
        btnAlertChat = (ImageView) findViewById(R.id.btn_alert_chat);

        emojiButton = (ImageView) findViewById(R.id.emoji_button);
        submitButton = (ImageView) findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(), rootView, emojiButton, emojiconEditText);
        emojIconActions.ShowEmojicon();

        localStorage = LocalStorage.getInstance(getApplicationContext());
        user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

        if (user == null) {
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

                    myXMPP.sendMessage(emojiconEditText.getText().toString());
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

        btnGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDrawer) {
                    drawerLayout.closeDrawer(leftRL);
                } else {
                    initDrawer();
                    drawerLayout.openDrawer(leftRL);
                }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isShowDrawer = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isShowDrawer = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        lvMembersGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(getApplicationContext(), ChatOneToOne.class);
//                i.putExtra("creating", true);
//                i.putExtra("jId", listDrawerAdapter.getItem(position).getjId());
//                startActivity(i);
            }
        });

        btnAlertChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatOneToOne.class);
                startActivity(i);
            }
        });
    }

    private void initXMPPServer() {
        if (user != null) {
            myXMPP = MyXMPP.getInstance(getApplicationContext(), com.rc.nowtv.utils.C.DOMAIN,
                    com.rc.nowtv.utils.C.URL_SERVER, user.getUsername(), user.getIdUser(),
                    new MyXMPP.ReceivedMessages() {
                        @Override
                        public void onReceived(final ChatMessage chatMessage) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshAdapter(chatMessage);
                                }
                            });
                        }

                        @Override
                        public void onReceived(Chat chat, Message message) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnAlertChat.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        public void onConnected() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initDrawer();
                                    getPastMessages();
                                }
                            });
                        }
                    });
        }
    }

    private void getPastMessages() {
        listMessages = myXMPP.getOldMessages();
        chatAdapter = new ChatAdapter(getApplicationContext(), 0, myXMPP.getOldMessages());

        listOfMessage.setAdapter(chatAdapter);
        listOfMessage.setVisibility(View.VISIBLE);
        scrollToLast();
    }

    private void refreshAdapter(ChatMessage chatMessage) {
        listMessages.add(chatMessage);
        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
        scrollToLast();
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

//        final VideoView videoView = (VideoView)findViewById(R.id.video_view);
////        videoView.setVideoURI(Uri.parse(C.URL_LIVE_VIDEO_TEST));
//        videoView.setVideoPath(C.URL_LIVE_VIDEO);
//        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//
//        videoView.setMediaController(new MediaController(this));
//        videoView.requestFocus();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            public void onPrepared(MediaPlayer mp) {
//                //progressDialog.dismiss();
//                videoView.seekTo(0);
//                videoView.start();
//                Log.d(TAG, "Onprepared ok, reproduzindo...");
//            }
//        });
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

    public void scrollToLast() {
        listOfMessage.clearFocus();
        listOfMessage.setSelection(chatAdapter.getCount() - 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (vodPlayer != null && vodPlayer.getExoPlayer() != null)
            vodPlayer.getExoPlayer().release();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vodPlayer != null && vodPlayer.getExoPlayer() != null)
            vodPlayer.getExoPlayer().release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        initXMPPServer();

        super.onConfigurationChanged(newConfig);
    }

}