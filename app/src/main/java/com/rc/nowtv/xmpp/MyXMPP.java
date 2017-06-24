package com.rc.nowtv.xmpp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rc.nowtv.R;
import com.rc.nowtv.models.ChatMessage;
import com.rc.nowtv.models.Member;
import com.rc.nowtv.models.User;
import com.rc.nowtv.models.Video;
import com.rc.nowtv.utils.C;
import com.rc.nowtv.utils.LocalStorage;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MUCNotJoinedException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by berg on 23/05/17.
 */

public class MyXMPP implements ConnectionListener, ChatManagerListener {
    private static final String TAG = MyXMPP.class.getSimpleName();

//    private XmppService contextXMPP;
    private Context context;

    private static MyXMPP instance;
    private static XMPPTCPConnection connection;
    private MultiUserChat mchat;
    private Chat chat;
    private LocalStorage localStorage;

    private String serviceName;
    private String hostAddress;
    private String username;
    private String password;
    private Video video;

    private ReceivedMessages receivedMessages;
    private ArrayList<Member> listMembers;

    private MyXMPP(Context context, String mServiceName, String mHostAddress, String username,
                   String password, Video video, ReceivedMessages receivedMessages) {
        this.serviceName = mServiceName;
        this.hostAddress = mHostAddress;
        this.username = username;
        this.password = password;
        this.context = context;
        this.receivedMessages = receivedMessages;
        this.listMembers = new ArrayList<>();
        this.video = video;
        init();
    }

    public static MyXMPP getInstance(Context context, String mServiceName, String mHostAddress, String user,
                                     String pass, Video video, ReceivedMessages receivedMessages) {
        if (instance == null) {
            instance = new MyXMPP(context, mServiceName, mHostAddress, user, pass, video, receivedMessages);
        }
        return instance;

    }

    public ArrayList<Member> getListMembers() {
        return listMembers;
    }

    public Chat getChat() {
        return chat;
    }

    private void init() {
        localStorage = LocalStorage.getInstance(context);
        new ConnectToXmppServer().execute();

    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                receivedMessages.onReceived(chat, message);
                Log.d(TAG, "Mensagem recebida em chat um pra um: " + message.getBody());
            }
        });
    }

    public void setOnReceived(ReceivedMessages received) {
        this.receivedMessages = received;
    }

    private class ConnectToXmppServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "Connecting to xmpp server started...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
                        .builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setServiceName(serviceName)
                        .setHost(hostAddress)
                        .setPort(8080)
                        .setDebuggerEnabled(true)
                        .setCompressionEnabled(false).build();
                connection = new XMPPTCPConnection(config);
                connection.setPacketReplyTimeout(60000);
                connection.addConnectionListener(MyXMPP.this);
                connection.connect();
            } catch (XMPPException | SmackException | IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(TAG, "Connecting to xmpp server finished...");
        }
    }

    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        Log.i(TAG, "Authenticated");
    }

    @Override
    public void connected(XMPPConnection arg0) {
        User user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

        Log.i(TAG, "Connected");
        try {
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

            if (!connection.isAuthenticated()) {

                AccountManager accountManager = AccountManager.getInstance(connection);
                Map<String, String> attributes = new HashMap<>();
                attributes.put("name", user.getName());
                attributes.put("email", user.getEmail());
                attributes.put("username", user.getUsername());
                attributes.put("url", user.getUrlPhoto());

                accountManager.createAccount(user.getUsername(), user.getIdUser(), attributes);

                login(user.getUsername(), user.getIdUser());
            }

        } catch (XMPPException | SmackException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            if (e.getMessage().equals("XMPPError: conflict - cancel")) {
                login(user.getUsername(), user.getIdUser());
            }
        }

        if (connection.isAuthenticated()) {
            Log.w("app", "Auth done");
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            MyXMPP.this.chat = chat;

                            chat.addMessageListener(new ChatMessageListener() {
                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    receivedMessages.onReceived(chat, message);
                                }
                            });
                        }
                    });
        }

        receivedMessages.onConnected();
    }

    private void login(String username, String id) {
        try {
            connection.login(username, id);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Presence presence = new Presence(Presence.Type.available);
        try {
            connection.sendPacket(presence);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        join(video.getRoom(), username);
    }

    @Override
    public void connectionClosed() {
        Log.i(TAG, "Connection closed");
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        Log.i(TAG, "Connection closed on error");
    }

    @Override
    public void reconnectingIn(int arg0) {
        Log.i(TAG, "Reconnecting in");
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        Log.i(TAG, "Reconnection failed");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i(TAG, "Reconnection successful");
    }

    public boolean join(String group, String username) {
        try {
            MultiUserChatManager mchatManager = MultiUserChatManager.getInstanceFor(connection);
            mchat = mchatManager.getMultiUserChat(group + "@" + C.GROUP_CHAT_DOMAIN);

//            DiscussionHistory history = new DiscussionHistory();
//            history.setMaxStanzas(5);
//            history.setSince(new Date());

            mchat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    Log.d("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '" + message);
                    if (message != null && message.getBody() != null) {
                        receivedMessages.onReceived(new ChatMessage(message.getBody(),
                                message.getFrom().substring(message.getFrom().lastIndexOf("/")+1, message.getFrom().length()), null));
                    }
                }
            });

            if (!mchat.isJoined()) {
                mchat.join(username, group/*, "", history, connection.getPacketReplyTimeout()*/);
                listMembers = getMembersGroup();
                System.out.println("The conference room success....");
            }
            return true;
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ChatMessage> getOldMessages() {
        ArrayList<ChatMessage> oldMessages = new ArrayList<>();
        if (mchat != null) {
            try {
                Message message = mchat.nextMessage();

                while (message != null) {
                    oldMessages.add(new ChatMessage(message.getBody(), message.getFrom(), null));
                    message = mchat.nextMessage();
                }
            } catch (MUCNotJoinedException e) {
                e.printStackTrace();
            }
        }
        return oldMessages;
    }

    public void sendMessage(String chatMessage) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        mchat = mchat == null? manager.getMultiUserChat(C.GROUP_NAME + "@" + C.GROUP_CHAT_DOMAIN) : mchat;

//        Message msg = new Message();
//        msg.setType(Message.Type.groupchat);
//        msg.setTo("redes2017@" + C.GROUP_CHAT_DOMAIN);
//        msg.setBody(chatMessage);


        if (!mchat.isJoined()) {
            try {
                mchat.join(username, C.GROUP_NAME);
                mchat.sendMessage(username + " entrou na sala " + C.GROUP_NAME);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

        Message message = new Message();
        message.setBody(chatMessage);
        message.setType(Message.Type.groupchat);

        try {
//            connection.sendPacket(msg);
            mchat.sendMessage(message);
            Log.d(TAG, "Mensagem enviada com sucesso!");
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Member> getMembersGroup() {
        if (connection == null) return new ArrayList<>();

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        mchat = mchat == null? manager.getMultiUserChat(C.GROUP_NAME + "@" + C.GROUP_CHAT_DOMAIN) : mchat;

        List<String> listUser = mchat.getOccupants();

        ArrayList<Member> memberList = new ArrayList<>();

        for (int i = 0; i < listUser.size(); i++) {
            String name = listUser.get(i);
            String username = name.substring(name.lastIndexOf("/")+1, name.length());
            Presence presence = mchat.getOccupantPresence(name);

            memberList.add(new Member(username, getStatus(presence), name));
        }
        return memberList;
    }

    public void createChatOneToOne(String jId) {
        if (connection == null) return;

        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        mchat = mchat == null? manager.getMultiUserChat(C.GROUP_NAME + "@" + C.GROUP_CHAT_DOMAIN) : mchat;

        chat = mchat.createPrivateChat(jId, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
            }
        });
    }

    private int getStatus(Presence presence) {
        int idPhoto = -1;
        if (presence == null || presence.getType() == null) return idPhoto;

        switch (presence.getType().name()) {
            case "AVAILABLE": idPhoto = R.mipmap.ic_online; break;
            case "UNAVAILABLE": idPhoto = R.mipmap.ic_offline; break;
            default: idPhoto = R.mipmap.ic_offline; break;
        }
        return idPhoto;
    }

    public interface ReceivedMessages {
        void onReceived(ChatMessage chatMessage);
        void onReceived(Chat chat, Message message);

        void onConnected();
    }
}
