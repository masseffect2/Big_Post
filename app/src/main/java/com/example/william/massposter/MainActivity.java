package com.example.william.massposter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AuthListener {
        //implements LoginListener, ExceptionHandler{

    TextView txtStatus;
    LoginButton facebookLoginButton;
    public CallbackManager callbackManager;
    TwitterLoginButton twitterLoginButton;
    private Button instaButton = null;
    private Button logoutButton = null;
    private String instaToken = null;
    private AuthInstagram authInstagram = null;
    private AppPreferences appPreferences = null;
    private View info = null;
    private Toolbar toolbar;
    //private DrawerLayout mDrawer;

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

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

       // mDrawer = findViewById(R.id.drawer_layout);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        txtStatus.setText("Login successful\n"+loginResult.getAccessToken());
                        Toast.makeText(MainActivity.this, "Logged in to Facebook", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Login cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, "Login failed. Verify the correct credentials have been entered "
                                + exception, Toast.LENGTH_LONG).show();
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

        logoutButton = findViewById(R.id.gotoPostPage);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostPage.class);
                startActivity(intent);
            }
        });

    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawer.openDrawer(GravityCompat.START);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    public void selectDrawerItem(MenuItem menuItem) {
//        // Create a new fragment and specify the fragment to show based on nav item clicked
//        Fragment fragment = null;
//        Class fragmentClass;
//        switch(menuItem.getItemId()) {
//            case R.id.nav_post:
//                //fragmentClass = FirstFragment.class;
//                Intent intent = new Intent(MainActivity.this, PostPage.class);
//                startActivity(intent);
//                break;
//            case R.id.nav_manage:
//                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent2);
//                //fragmentClass = SecondFragment.class;
//                break;
//        }
//
////        try {
////            fragment = (Fragment) fragmentClass.newInstance();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        // Insert the fragment by replacing any existing fragment
////        FragmentManager fragmentManager = getSupportFragmentManager();
////        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
//
//        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle(menuItem.getTitle());
//        // Close the navigation drawer
//        mDrawer.closeDrawers();
//    }
////
////    @Override
////    protected void onResume() {
////        super.onResume();
////        findViewById(R.id.tumblrLoginButton).setOnClickListener(clickListener);
////    }
////

    //this login method for Instagram changes the button text
    public void instaLogin() {
        //instaButton.setText("Instagram Logout");
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
        //instaButton.setText("Instagram Logout");
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
            try {
                com.squareup.okhttp.Response response = client.newCall(request).execute();
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

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

}
