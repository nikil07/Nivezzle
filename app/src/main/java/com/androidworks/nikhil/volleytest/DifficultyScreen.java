package com.androidworks.nikhil.volleytest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.nikhil.volley.dbutils.DBHelper;

import io.fabric.sdk.android.Fabric;

public class DifficultyScreen extends BaseGameActivity {

    DBHelper helper_ob;
    SQLiteDatabase db_ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_difficulty_screen);


    }

    public void easyClick(View view) {
        Intent intent =  new Intent(this,MainActivity.class);
        intent.putExtra("difficulty","easy");
        startActivity(intent);
    }

    public void mediumClick(View view) {
        Intent intent =  new Intent(this,MainActivity.class);
        intent.putExtra("difficulty","medium");
        startActivity(intent);
    }

    public void hardClick(View view) {
        Intent intent =  new Intent(this,MainActivity.class);
        intent.putExtra("difficulty","hard");
        startActivity(intent);
    }

    public void signIn(View view) {
        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
    }
    public void showLeaderboard(View view) {

        if (getApiClient().isConnected()){
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                        getApiClient(), getString(R.string.nivezzle_leaderboard)),
                2);
    }}

    public void resetDB(View view) {

        helper_ob = new DBHelper(this,null,null,1);
        db_ob = helper_ob.getWritableDatabase();

        helper_ob.onUpgrade(db_ob,1,1);
    }
}
