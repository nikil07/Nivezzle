package com.androidworks.nikhil.volleytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainAnswerScreenActivity extends Activity {

    static final String TAG="Nikhil";
    //private NetworkImageView mainPuzzleImage;
    private ImageView mainPuzzleImage;
    private EditText guessBox;
    private Button submitButton;
    PhotoViewAttacher attacher;
    static List<String> imageURLLIST = MainActivity.sendImageURLList();
    static List<String> answerKeys = MainActivity.sendAnswerKeys();
    int rowValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_answer_screen);
        mainPuzzleImage = (ImageView) findViewById(R.id.mainPuzzleImage);
        guessBox = (EditText) findViewById(R.id.guessBox);
        submitButton = (Button) findViewById(R.id.submitButton);
        Bundle bundle = getIntent().getExtras();
        rowValue = bundle.getInt("rowValue")  ;

        Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if(attacher!=null){
                    attacher.update();
                }else{
                    attacher = new PhotoViewAttacher(mainPuzzleImage);
                }
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        };

       // ImageLoader.ImageCache imageCache = new LruBitmapCache();
       // ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(MainAnswerScreenActivity.this), imageCache);
       // mainPuzzleImage.setImageUrl(imageURLLIST.get(rowValue),imageLoader);

        Picasso.with(this)
                .load(imageURLLIST.get(rowValue))
                .placeholder(R.mipmap.launcher_icon_1).error(R.mipmap.launcher_icon_1)
                .into(mainPuzzleImage,imageLoadedCallback);



    }

    public void submitButton(View view)
    {


        if(guessBox.getText().toString().trim().equalsIgnoreCase(answerKeys.get(rowValue)))
        {

            Context context=getApplicationContext();
            LayoutInflater inflater=getLayoutInflater();
            View customToastRoot =inflater.inflate(R.layout.custom_toast, null);
            Toast customToast=new Toast(context);
            customToast.setView(customToastRoot);
            customToast.setDuration(Toast.LENGTH_LONG);
            customToast.show();


        }
        else
        {
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            guessBox.startAnimation(shake);

        }
    }

}
