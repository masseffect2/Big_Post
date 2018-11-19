package com.example.william.massposter;


import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AuthInstagram extends Dialog {

    private final String redirect_url;
    private final String request_url;
    private AuthListener listener;

    public AuthInstagram(@NonNull Context context, AuthListener listener) {
        super(context);
        this.listener = listener;
        this.redirect_url = context.getResources().getString(R.string.instagram_redirect_url);
        this.request_url = context.getResources().getString(R.string.instagram_base_url) +
                "oauth/authorize/?client_id=" +
                context.getResources().getString(R.string.INSTAGRAM_CLIENT_ID) +
                "&redirect_uri=" + redirect_url +
                "&response_type=token&display=touch&scope=public_content";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.insta_dialog);
        initializeWebView();
    }

    private void initializeWebView() {
        try {
            WebView webView = findViewById(R.id.webView);
            WebSettings settings = webView.getSettings();
            settings.setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(request_url);
            webView.setWebViewClient(webViewClient);
        } catch (Exception ex){
            System.out.println(ex);
    }
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(redirect_url)) {
                AuthInstagram.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("access_token=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                Log.e("access_token", access_token);
                listener.onTokenReceived(access_token);
                dismiss();
            } else if (url.contains("?error")) {
                Log.e("access_token", "getting error fetching access token");
                dismiss();
            }
        }
    };

}