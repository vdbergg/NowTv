package com.rc.nowtv.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.rc.nowtv.R;
import com.rc.nowtv.models.User;
import com.rc.nowtv.utils.LocalStorage;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private View rootView;
    private FloatingActionButton btnLive;

    private GoogleApiClient mGoogleApiClient;
    private Button btnLoginGoogle;

    private Button btnLoginFacebook;
    private CallbackManager callbackManager;
    private XMPPTCPConnection connection;
    private MultiUserChat mchat;

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
        btnLoginGoogle = (Button) rootView.findViewById(R.id.btn_sign_in_gmail);
        btnLoginFacebook = (Button) rootView.findViewById(R.id.btn_login_facebook);
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

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
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

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

//        new ConnectToXmppServer().execute();

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

}
