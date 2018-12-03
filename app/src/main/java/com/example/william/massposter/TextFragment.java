package com.example.william.massposter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.william.massposter.MainActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class TextFragment extends Fragment {
    private Button btnPost;
    private EditText textBox;
    private ShareButton btnFacebookPost;
    private String message;
    private CheckBox facebookCheck;
    private CheckBox twitterCheck;
    private CheckBox instagramCheck;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle SaveInstanceState){
        View view = inflater.inflate(R.layout.fragment_text,container,false);
        btnPost = view.findViewById(R.id.postButton);
        textBox = view.findViewById(R.id.postTextbox);
        btnFacebookPost = view.findViewById(R.id.btnFacebookPost);
        facebookCheck = view.findViewById(R.id.chkFacebook);
        twitterCheck = view.findViewById(R.id.chkTwitter);
        instagramCheck = view.findViewById(R.id.chkInstagram);


        btnPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                message = textBox.getText().toString();
                if(!message.isEmpty()) {
                    if(twitterCheck.isChecked()) {
                        if(message.length() < 281) {
                            ((PostPage) getActivity()).testMethod(message);
                            final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                    .getActiveSession();
                            final Intent intent = new ComposerActivity.Builder(getActivity())
                                    .session(session)
                                    .text(message)
                                    .hashtags("#test")
                                    .createIntent();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Message must be under 280 characters"
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                    if(facebookCheck.isChecked()) {
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                                .setQuote(message)
                                .build();
//                        ShareApi fbApi = new ShareApi(content);
//                        fbApi.setMessage(message);
//                        MainActivity main = new MainActivity();
//                        fbApi.share(content, null);
//                        ShareLinkContent content = new ShareLinkContent.Builder()
//                                .setQuote(message)
//                                .build();
//
                        btnFacebookPost.setShareContent(content);
                    }
                } else {
                    Toast.makeText(getActivity(), "You can't make a blank post..."
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

}
