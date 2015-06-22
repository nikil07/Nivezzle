package com.androidworks.nikhil.volleytest;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class ParseInit extends Application{


    @Override
    public void onCreate() {

        Parse.initialize(this, "pmcDH4Pup8pFfnwDgmsLy1FpZ3l07IFO5BqUfd7D", "iBjkkhSZ7biYqnKrbyiDJzZEc8sQPDLjd9nLf8Gm");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
