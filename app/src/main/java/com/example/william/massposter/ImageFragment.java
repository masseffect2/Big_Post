package com.example.william.massposter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.io.File;

public class ImageFragment extends Fragment {

    private Button btnPost;
    private ImageButton btnImage;
    private EditText textBox;
    private String message;
    private CheckBox facebookCheck;
    private CheckBox twitterCheck;
    private CheckBox instagramCheck;
    private Uri selectedImage;
    private static int RESULT_LOAD_IMAGE = 1;
    private ShareButton btnFacebookPost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle SaveInstanceState){
        View view = inflater.inflate(R.layout.fragment_image,container,false);
        btnPost = view.findViewById(R.id.postButtonImage);
        btnImage = view.findViewById(R.id.btnImage);
        textBox = view.findViewById(R.id.postTextbox);
        facebookCheck = view.findViewById(R.id.chkFacebook);
        twitterCheck = view.findViewById(R.id.chkTwitter);
        instagramCheck = view.findViewById(R.id.chkInstagram);
        btnFacebookPost = view.findViewById(R.id.btnFacebookPost);

        btnImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, RESULT_LOAD_IMAGE);
                selectedImage = gallery.getData();
// File path = Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES);
//                file = new File(path, "uploaded_image");
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!selectedImage.getPath().isEmpty()) {
                    if (twitterCheck.isChecked()) {
                        ((PostPage) getActivity()).testMethod(message);
                        final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                .getActiveSession();
                        final Intent intent = new ComposerActivity.Builder(getActivity())
                                .session(session)
                                .image(selectedImage)
                                .hashtags("#twitter")
                                .createIntent();
                        startActivity(intent);
                    }
                    if (facebookCheck.isChecked()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(selectedImage.getPath());
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();
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
