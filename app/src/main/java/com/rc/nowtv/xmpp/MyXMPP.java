package com.rc.nowtv.xmpp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rc.nowtv.models.ChatMessage;
import com.rc.nowtv.models.User;
import com.rc.nowtv.utils.C;
import com.rc.nowtv.utils.LocalStorage;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MUCNotJoinedException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tv.icomp.vod.vodplayer.log.LogManager;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by berg on 23/05/17.
 */

public class MyXMPP implements ConnectionListener {
    private static final String TAG = MyXMPP.class.getSimpleName();

//    private XmppService contextXMPP;
    private Context context;

    private static MyXMPP instance;
    private static XMPPTCPConnection connection;
    private MultiUserChat mchat;
    private LocalStorage localStorage;

    private String serviceName;
    private String hostAddress;
    private String username;
    private String password;

    private ReceivedMessages receivedMessages;

    private MyXMPP(Context context, String mServiceName, String mHostAddress, String username,
                   String password, ReceivedMessages receivedMessages) {
        this.serviceName = mServiceName;
        this.hostAddress = mHostAddress;
        this.username = username;
        this.password = password;
        this.context = context;
        this.receivedMessages = receivedMessages;
        init();
    }

    public static MyXMPP getInstance(Context context, String mServiceName, String mHostAddress, String user,
                                     String pass, ReceivedMessages receivedMessages) {
        if (instance == null) {
            instance = new MyXMPP(context, mServiceName, mHostAddress, user, pass, receivedMessages);
        }
        return instance;

    }

    private void init() {
        localStorage = LocalStorage.getInstance(context);
        new ConnectToXmppServer().execute();

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
                        .setPort(5222)
                        .setCompressionEnabled(false).build();
                connection = new XMPPTCPConnection(config);
                connection.setPacketReplyTimeout(1000);
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

        join("redes2017", username);
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
            mchat = mchatManager.getMultiUserChat(group + "@" + C.GROUP_CHAT_SERVER);

//            DiscussionHistory history = new DiscussionHistory();
//            history.setMaxStanzas(0);
            //history.setSince(new Date());

            mchat.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '" + message);
//                    Log.d(TAG, "Body: " + message.getBody());
//                    Log.d(TAG, "Subject: " + message.getSubject());
//                    Log.d(TAG, "From: " + message.getFrom());
//                    Log.d(TAG, "To: " + message.getTo());
//                    Log.d(TAG, "StanzaId: " + message.getStanzaId());
//                    Log.d(TAG, "Thread: " + message.getThread());
//                    Log.d(TAG, "Type: " + message.getType().name());
                    if (message != null && message.getBody() != null) {
                        receivedMessages.onReceived(new ChatMessage(message.getBody(),
                                message.getFrom().substring(message.getFrom().lastIndexOf("/")+1, message.getFrom().length()), null));
                    }
                }
            });

           // if (!mchat.isJoined()) {
                mchat.createOrJoin(username/*, "", history, connection.getPacketReplyTimeout()*/);
                System.out.println("The conference room success....");
            //}

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

    public List<RosterEntry> getEntriesByGroup(String groupName) {
        if (connection == null)
            return null;
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
//        RosterGroup rosterGroup = connection..getRoster().getGroup(groupName);
//        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
//        Iterator<RosterEntry> i = rosterEntry.iterator();
//        while (i.hasNext()) {
//            Entrieslist.add(i.next());
//        }
        return Entrieslist;
    }

    public void sendMessage(String message) throws XMPPException, SmackException.NotConnectedException {
        if (mchat != null ) {
            mchat.sendMessage(message);
            Log.d(TAG, "mensagem enviada para grupo!!!");
        } else
            Log.d(TAG, "mChat eh nulo!!!");
    }

    public interface ReceivedMessages {
        void onReceived(ChatMessage chatMessage);
    }
}