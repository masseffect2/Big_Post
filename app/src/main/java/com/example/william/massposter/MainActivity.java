package com.example.william.massposter;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.loglr.Interfaces.ExceptionHandler;
import com.tumblr.loglr.Interfaces.LoginListener;
import com.tumblr.loglr.Loglr;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
        //implements LoginListener, ExceptionHandler{

    TextView txtStatus;
    LoginButton facebookLoginButton;
    CallbackManager callbackManager;
    TwitterLoginButton twitterLoginButton;
    public final static String TUMBLR_CONSUMER_KEY = "ENTER-CONSUMER-KEY";

    public final static String TUMBLR_CONSUMER_SECRET = "ENTER-CONSUMER-SECRET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        initializeControls();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        txtStatus.setText("Login successful\n"+loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        txtStatus.setText("Login cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        txtStatus.setText("Login error: "+exception.getMessage());
                    }
                });

        twitterLoginButton = findViewById(R.id.twitterLoginButton);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                login(session);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Login failed. Verify the correct credentials have been entered "
                + exception, Toast.LENGTH_LONG).show();
            }
        });

        LoginListener loginListener = new LoginListener() {
            @Override
            public void onLoginSuccessful(@NotNull com.tumblr.loglr.LoginResult loginResult) {
                String strOAuthToken = loginResult.getOAuthToken();
                String strOAuthTokenSecret = loginResult.getOAuthTokenSecret();
            }
//            @Override
//            public void onLoginFailed(RuntimeException exception) {
//                Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
//            }
        };

        Loglr.getInstance()

                .setConsumerKey(TUMBLR_CONSUMER_KEY)

                .setConsumerSecretKey(TUMBLR_CONSUMER_SECRET)

                .setLoginListener(loginListener);

//                .setExceptionHandler(exceptionHandler)
//
//                .enable2FA(true)
//
//                .setUrlCallBack(strUrlCallback)
//
//                .initiateInActivity(AccountActivity.this);
    }

    private void initializeControls() {
        callbackManager = CallbackManager.Factory.create();
        txtStatus = findViewById(R.id.txtStatus);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);
    }

    public void login(TwitterSession session){
        String username =  session.getUserName();
        Intent intent = new Intent(MainActivity.this, PostPage.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void LoginWithFB() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }



}
