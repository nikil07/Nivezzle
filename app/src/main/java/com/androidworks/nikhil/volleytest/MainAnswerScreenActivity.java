package com.androidworks.nikhil.volleytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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
import com.squareup.picasso.Target;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainAnswerScreenActivity extends Activity {

    static final String TAG="Nikhil";
    static List<String> imageURLLIST = MainActivity.sendImageURLList();
    static List<String> answerKeys = MainActivity.sendAnswerKeys();
    PhotoViewAttacher attacher;
    int rowValue;
    //private NetworkImageView mainPuzzleImage;
    private ImageView mainPuzzleImage;
    private EditText guessBox;
    private Button submitButton;

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

        Target target = new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

              //  Log.d(tag,"inside file");

                try {
                    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File myDir = new File(root + "/yourDirectory");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
            //        Log.d(tag,String.valueOf(myDir));
                    String name = new Date().toString() + ".jpg";
                    myDir = new File(myDir, name);
                    FileOutputStream out = new FileOutputStream(myDir);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch(Exception e){
                    //Log.d(tag,e.toString());
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
              //  Log.d(tag,"inside failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
              //  Log.d(tag,"inside prepareload");
            }
        };
        Picasso.with(this)
                .load(imageURLLIST.get(rowValue))
                .placeholder(R.mipmap.launcher_icon_1).error(R.mipmap.launcher_icon_1)
                .into(target);

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
            mainPuzzleImage.startAnimation(shake);
            submitButton.startAnimation(shake);
        }
    }

}
