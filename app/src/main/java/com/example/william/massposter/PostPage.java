package com.example.william.massposter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

public class PostPage extends AppCompatActivity {

    private PageAdapter mPageAdapter;

    private ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postpage);
//        String username = getIntent().getStringExtra("username");
//        TextView uname = findViewById(R.id.TV_username);
//        uname.setText(username);

        mPageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewAdapter(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewAdapter(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TextFragment(), "Text Post");
        adapter.addFragment(new ImageFragment(), "Image Post");
        viewPager.setAdapter(adapter);
    }

    public void testMethod (String message){
        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                .getActiveSession();
        final Intent intent = new ComposerActivity.Builder(PostPage.this)
                .session(session)
                .text(message)
                .hashtags("#twitter")
                .createIntent();
        startActivity(intent);
    }
}
