package com.example.william.massposter;

import android.content.Intent;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.okhttp.Request;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements AuthListener {
        //implements LoginListener, ExceptionHandler{

    TextView txtStatus;
    LoginButton facebookLoginButton;
    CallbackManager callbackManager;
    TwitterLoginButton twitterLoginButton;
    private Button instaButton = null;
    private Button logoutButton = null;
    private String instaToken = null;
    private AuthInstagram authInstagram = null;
    private AppPreferences appPreferences = null;
    private View info = null;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        instaButton = findViewById(R.id.instagramLoginButton);
        info = findViewById(R.id.info);
        appPreferences = new AppPreferences(this);

        setContentView(R.layout.activity_main);
        initializeControls();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);

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

        instaToken = appPreferences.getString(AppPreferences.TOKEN);
        if (instaToken != null) {
            getUserInfoByAccessToken(instaToken);
        }

//        logoutButton = findViewById(R.id.instgramLogoutButton);
//        logoutButton.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        findViewById(R.id.tumblrLoginButton).setOnClickListener(clickListener);
//    }
//
//    private View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Loglr.getInstance()
//                    .setConsumerKey("sdJk9ZkYwCHLvAir7JHNVP19ioUvpdFv8H0W1T1TGw1hqEjq13")
//                    .setConsumerSecretKey("APwuoDYsScGZn75ceyNOa05djM9aT0cFRNaSmgbc2JDmFKutBU")
//                    .setUrlCallBack(getResources().getString(R.string.tumblr_callback_url))
//                   // .setLoadingDialog(LoadingDialog.class)
//                    .setLoginListener(MainActivity.this)
//                    .setExceptionHandler(MainActivity.this)
//                    .enable2FA(true)
//                    .initiateInActivity(MainActivity.this);
//        }
//    };


    //this login method for Instagram changes the button text
    public void instaLogin() {
        //instaButton.setText("LOGOUT");
        //info.setVisibility(View.VISIBLE);
        ImageView pic = findViewById(R.id.pic);
        Picasso.with(this).load(appPreferences.getString(AppPreferences.PROFILE_PIC)).into(pic);
        TextView id = findViewById(R.id.id);
        id.setText(appPreferences.getString(AppPreferences.USER_ID));
        TextView name = findViewById(R.id.name);
        name.setText(appPreferences.getString(AppPreferences.USER_NAME));
//        Intent intent = new Intent(MainActivity.this, PostPage.class);
//        startActivity(intent);
    }

    public void logout() {
        //instaButton.setText("INSTAGRAM LOGIN");
        instaToken = null;
        info.setVisibility(View.GONE);
        appPreferences.clear();
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

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        appPreferences.putString(AppPreferences.TOKEN, auth_token);
        instaToken = auth_token;
        getUserInfoByAccessToken(instaToken);
    }

//    @Override
//    public void onClick(View view) {
//        if (instaToken!=null){
//            logout();
//        } else {
//            authInstagram = new AuthInstagram(this, this);
//            authInstagram.setCancelable(true);
//            authInstagram.show();
//        }
//    }

    private void getUserInfoByAccessToken(String instaToken) {
        new RequestInstagramAPI().execute();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {
        com.squareup.okhttp.OkHttpClient client =new com.squareup.okhttp.OkHttpClient();
        //OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(Void... params) {
            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.get_user_info_url) + instaToken)
                    .build();

//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + instaToken);
            try {
                com.squareup.okhttp.Response response = client.newCall(request).execute();

//                org.apache.http.HttpResponse response = httpClient.execute(httpGet);
                //HttpEntity httpEntity = response.getEntity();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));


                        instaLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
