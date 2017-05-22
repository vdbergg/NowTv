package com.rc.nowtv.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.rc.nowtv.R;
import com.rc.nowtv.models.User;
import com.rc.nowtv.utils.C;
import com.rc.nowtv.utils.LocalStorage;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static org.jivesoftware.smackx.pubsub.ChildrenAssociationPolicy.owners;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, ConnectionListener {
    private View rootView;
    private FloatingActionButton btnLive;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnLoginGoogle;

    private LoginButton btnLoginFacebook;
    private CallbackManager callbackManager;
    private XMPPTCPConnection connection;

    private boolean authenticated;
    private boolean connected;
    private boolean chat_created;
    private boolean loggedin;

    private LocalStorage localStorage;

    private static final int RC_SIGN_IN = 007;


    public LoginFragment() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity());

        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        initView();
        initListener();
        initVariables();
        initValue();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                handleSignInResult(result);
            } else {
                Toast.makeText(rootView.getContext(), rootView.getResources()
                        .getString(R.string.connection_error_message), Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(rootView.getContext(), getString(R.string.connection_error_message), Toast.LENGTH_SHORT).show();
    }

    private void initView() {
//        btnLive = (FloatingActionButton) rootView.findViewById(R.id.btn_live);
        btnLoginGoogle = (SignInButton) rootView.findViewById(R.id.btn_sign_in_gmail);
        btnLoginFacebook = (LoginButton)rootView.findViewById(R.id.btn_login_facebook);
    }

    private void initListener() {
//        btnLive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(rootView.getContext(), PlayerFragment.class));
//            }
//        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void initVariables() {
        localStorage = LocalStorage.getInstance(rootView.getContext());
    }

    private void initValue() {
        initLoginGoogle();
        initLoginFacebook();
    }

    private void initLoginFacebook() {
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook.setReadPermissions("email", "public_profile");

        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleLoginFacebookResult(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(rootView.getContext(), rootView.getResources()
                        .getString(R.string.connection_error_message), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginFacebookResult(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");
                            String idUser = loginResult.getAccessToken().getUserId();
                            String urlPhoto = "https://graph.facebook.com/" + idUser + "/picture?type=large"; // Imagem pequena
                            String fullname = firstName + " " + lastName;

                            registerUser(fullname, email, idUser, urlPhoto);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void initLoginGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(rootView.getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            btnLoginGoogle.setVisibility(View.GONE);
        } else {
            localStorage.addToStorage(LocalStorage.USER, null);
        }
        btnLoginGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnLoginGoogle.setScopes(gso.getScopeArray());
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String fullname = acct.getDisplayName();
            String email = acct.getEmail();
            String idUser = acct.getId();
            String id = acct.getIdToken();
            String urlPhoto = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "";

            Log.d(TAG, "IdUser: " + idUser);
            Log.d(TAG, "Token: " + id);
            Log.d(TAG, "Primeira parte email: " + email.substring(0, email.lastIndexOf("@")));


            registerUser(fullname, email, idUser, urlPhoto);
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void registerUser(String fullname, String email, String idUser, String urlPhoto) {
        User user = new User(fullname, email, idUser, urlPhoto);
        localStorage.addToStorage(LocalStorage.USER, user);

        String username = email.substring(0, email.lastIndexOf("@"));
        new ConnectToXmppServer().execute();
//        MyLoginTask task = new MyLoginTask(username, idUser);
//        task.execute("");
        //initializeConnection();

        Snackbar.make(rootView, "Logado com sucesso!!", Snackbar.LENGTH_SHORT).show();
    }

    public void signOut() {
        if (mGoogleApiClient != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Snackbar.make(rootView, getString(R.string.logout_message), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }

        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
    }


    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        Log.i(TAG, "Authenticated");
    }

    @Override
    public void connected(XMPPConnection arg0) {
        Log.i(TAG, "Connected");
        try {
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");

            if (!authenticated)
                connection.login("admin", "adminrc");
            else {
                if (!connection.isAuthenticated()) {
                    User user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

                    String username = user.getEmail().substring(0, user.getEmail().lastIndexOf("@"));
                    connection.login(username, user.getIdUser());
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendPacket(presence);
                }
            }

            if (connection.isAuthenticated() && !authenticated) {
                authenticated = true;
//                Toast.makeText(rootView.getContext(), "Conectado e logado ao servidor OPENFIRE", Toast.LENGTH_SHORT).show();
                AccountManager accountManager = AccountManager.getInstance(connection);
//                accountManager.sensitiveOperationOverInsecureConnection(true);
                User user = localStorage.getObjectFromStorage(LocalStorage.USER, User.class);

                String username = user.getEmail().substring(0, user.getEmail().lastIndexOf("@"));
                accountManager.createAccount(username, user.getIdUser());
                connection.disconnect();
                connection.connect();
            } else if (connection.isAuthenticated() && authenticated) {
                createChat(connection);
            }

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
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
                        .setServiceName(C.DOMAIN)
                        .setHost(C.URL_SERVER)
                        .setPort(5222)
                        .setCompressionEnabled(false).build();
                connection = new XMPPTCPConnection(config);
                connection.setPacketReplyTimeout(1000);
                connection.addConnectionListener(LoginFragment.this);
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

//    private class MyLoginTask extends AsyncTask<String, String, String> {
//        private String username;
//        private String password;
//
//        public MyLoginTask(String username, String password) {
//            this.username = username;
//            this.password = password;
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
//                    //.setUsernameAndPassword("berg", "041097")
//                    .setHost(C.URL_SERVER)
//                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                    .setServiceName(C.DOMAIN)
//                    .setPort(5222)
//                    .setDebuggerEnabled(true)
//                    .build();
//
//            AbstractXMPPConnection con1 = new XMPPTCPConnection(config);
//
//            try {
//                con1.connect();
//
//                con1.login("admin", "admin");
//
//                if (con1.isAuthenticated()) {
//                    AccountManager accountManager = AccountManager.getInstance(con1);
////                    accountManager.sensitiveOperationOverInsecureConnection(true);
//                    accountManager.createAccount(username, password);
//                    con1.disconnect();
//                    con1.connect();
//                    // The account has been created, so we can now login
//                    con1.login(username, password);
//
//                    Presence presence = new Presence(Presence.Type.available);
//                    con1.sendPacket(presence);
//                }
//
//                if (con1.isAuthenticated()) {
//                    Log.d(TAG, "Authentication done");
//
//                    createChat(con1);
//                }
//            } catch (SmackException | IOException | XMPPException e) {
//                Log.d(TAG, e.getMessage());
//                if(e.getMessage().equals("XMPPError: conflict - cancel")) {
//                    con1.disconnect();
//                    try {
////                        if (!con1.isConnected())
////                            con1.connect();
//
//                        con1.login(username, password);
//
//                        createChat(con1);
//                    } catch (SmackException | IOException | XMPPException e2) {
//                        Log.d(TAG, e2.toString());
//                    }
//                }
//            }
//
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//
//        }
//    }

    private void createChat(AbstractXMPPConnection con1) {
        if (con1.isAuthenticated()) {
            Log.d(TAG, "Authentication done");

            final ChatManager chatManager = ChatManager.getInstanceFor(con1);
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                            System.out.println("Received message: " + (message != null? message.getBody() : "NULL"));
                            try {
                                chatManager.getThreadChat(chat.getThreadID()).sendMessage(new Message(message.getFrom(), "Resposta de retorno!"));
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                                Log.d(TAG, "Erro ao responder mensagem");
                            }
                        }
                    });

                    Log.d(TAG, chat.toString());
                }
            });
        }
    }

}
