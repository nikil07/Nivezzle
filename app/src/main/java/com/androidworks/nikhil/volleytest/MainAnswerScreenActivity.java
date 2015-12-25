package com.androidworks.nikhil.volleytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.nikhil.volley.dbutils.DBAdapter;
import com.nikhil.volley.util.LeaderBoardChecklist;
import com.nikhil.volley.util.LruBitmapCache;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainAnswerScreenActivity extends BaseGameActivity {

    static final String TAG="Nikhil";
    static List<String> imageURLLIST = MainActivity.sendImageURLList();
    static List<String> answerKeys = MainActivity.sendAnswerKeys();
    static List<Integer> pointsList = MainActivity.sendPoints();
    List<String> cluesList;
    PhotoViewAttacher attacher;
    DBAdapter adapter;
    int rowValue;
    ScrollView mainAnswerLayout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences jsonPreferences;
    SharedPreferences.Editor jsonEditor;
    SharedPreferences hintsPreferences;
    SharedPreferences.Editor hintsEditor;
    int hintCounter=0;
    private NetworkImageView mainPuzzleImage;
   // private ImageView mainPuzzleImage;
    private EditText guessBox;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_answer_screen);
        mainPuzzleImage = (NetworkImageView) findViewById(R.id.mainPuzzleImage);
        guessBox = (EditText) findViewById(R.id.guessBox);
        sharedPreferences = getSharedPreferences("linksPath", Context.MODE_PRIVATE);

        submitButton = (Button) findViewById(R.id.submitButton);
        Bundle bundle = getIntent().getExtras();
        rowValue = bundle.getInt("rowValue");



       // name = "Nivezzle" + String.valueOf(rowValue);
        //filePath = sharedPreferences.getString(name, null);

//        File file = new File(filePath);

        /*Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if (attacher != null) {
                    attacher.update();
                } else {
                    attacher = new PhotoViewAttacher(mainPuzzleImage);
                }
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub

            }
        };
*/
         ImageLoader.ImageCache imageCache = new LruBitmapCache();
         ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(MainAnswerScreenActivity.this), imageCache);
         mainPuzzleImage.setImageUrl(imageURLLIST.get(rowValue), imageLoader);

       // Log.d(TAG, imageURLLIST.get(rowValue));
    }

    public void submitButton(View view) throws InterruptedException {

        sharedPreferences = getSharedPreferences("pointsCounter",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mainAnswerLayout = (ScrollView)findViewById(R.id.main_answer_layout);

        if(guessBox.getText().toString().trim().equalsIgnoreCase(answerKeys.get(rowValue)))
        {


            Set<String> set ;
            set = LeaderBoardChecklist.getCheckList();
/*

            Context context=getApplicationContext();
            LayoutInflater inflater=getLayoutInflater();
            View customToastRoot =inflater.inflate(R.layout.custom_toast, null);
            Toast customToast=new Toast(context);
            customToast.setView(customToastRoot);
            customToast.setDuration(Toast.LENGTH_LONG);
            customToast.show();*/
            editor.putInt("pointsCounter",sharedPreferences.getInt("pointsCounter",0)+pointsList.get(rowValue));
            editor.commit();

            Snackbar snackbar = Snackbar.make(mainAnswerLayout, "You guessed correctly", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
           // Toast.makeText(this, "Points = " + pointsList.get(rowValue), Toast.LENGTH_SHORT).show();

            Cursor pointsValues = adapter.queryName();

            if (pointsValues.moveToFirst()){
                do{
                    String data = pointsValues.getString(pointsValues.getColumnIndex("POINTS"));
                  //  Log.d(TAG,"data " +data);
                    set.add(data);
                    // do what ever you want here
                }while(pointsValues.moveToNext());
            }
            pointsValues.close();

            Log.d(TAG, pointsValues.toString());

            if(set.contains(answerKeys.get(rowValue))) {
              //  Toast.makeText(this, "inside if of duplicate", Toast.LENGTH_SHORT).show();
              //  Log.d(TAG, "inside if of duplicate");
            }
            else {
                // do stuff here
               // Toast.makeText(this, "inside else of duplicate", Toast.LENGTH_SHORT).show();
                Games.Leaderboards.submitScore(getApiClient(),
                        getString(R.string.nivezzle_leaderboard),
                        sharedPreferences.getInt("pointsCounter", 0));

                adapter.insertDetails(answerKeys.get(rowValue));
            }
           // Log.d(TAG,set.toString());

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // removing the activity
                    finish();
                }
            }, 3000);
        }
        else
        {
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            guessBox.startAnimation(shake);
            mainPuzzleImage.startAnimation(shake);
            submitButton.startAnimation(shake);
        }
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new DBAdapter(this);
    }

    public List<String> getClues()  {

        jsonPreferences = getSharedPreferences("JSONPref",Context.MODE_PRIVATE);
        String cluesJson = jsonPreferences.getString("cluesJson",null);
        List<String> cluesList = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        rowValue = bundle.getInt("rowValue");
        Log.d(TAG,cluesJson);
        JSONObject cluesObject = null;
        try {
            cluesObject = new JSONObject(cluesJson);
            Log.d(TAG,answerKeys.get(rowValue));
            JSONArray cluesArray = cluesObject.getJSONArray(answerKeys.get(rowValue));

            for(int i=0;i<cluesArray.length();i++)
            {
                cluesList.add(cluesArray.getString(i));
            }
        } catch (JSONException e) {
            Toast.makeText(this,"There was an error retrieving the clues",Toast.LENGTH_SHORT).show();
           return  null;
        }


        Log.d(TAG,cluesList.toString());

        return cluesList;

    }

    public void showHint(View view) {

        Bundle bundle = getIntent().getExtras();
        rowValue = bundle.getInt("rowValue");
        hintsPreferences.getInt(answerKeys.get(rowValue),0);
        cluesList = getClues();
        hintsPreferences = getSharedPreferences("hintsPref", Context.MODE_PRIVATE);
        hintsEditor = hintsPreferences.edit();
        hintsEditor.putInt(answerKeys.get(rowValue), hintCounter);
        hintsEditor.commit();
        if(hintsPreferences.getInt(answerKeys.get(rowValue),0)<3)
        {
          Toast.makeText(this,cluesList.get(hintsPreferences.getInt(answerKeys.get(rowValue),0)),Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this,"hints over",Toast.LENGTH_SHORT).show();
        }
        hintCounter++;
        if(hintCounter>3) hintCounter = hintCounter%3;


    }

}
