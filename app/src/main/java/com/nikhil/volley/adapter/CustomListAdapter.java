package com.nikhil.volley.adapter;

import com.androidworks.nikhil.volleytest.MainActivity;
import com.androidworks.nikhil.volleytest.R;
import com.nikhil.volley.app.AppController;
import com.nikhil.volley.model.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CustomListAdapter extends BaseAdapter {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String tag = "Nikhil";
    int index=0;
    String link="";
    String path="";
    String name = "";
    SharedPreferences sharedPreferences,prefForFirstRun;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Movie> movieItems;

    public CustomListAdapter(Activity activity, List<Movie> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;

    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        sharedPreferences = activity.getSharedPreferences("linksPath", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        prefForFirstRun = activity.getSharedPreferences("firstRun", Context.MODE_PRIVATE);
        boolean firstRun = prefForFirstRun.getBoolean("initialRun",true);

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        name = "Nivezzle" + String.valueOf(position);
        final File myinitialDir = new File(root + "/Nivezzle/");
        final  File myDir = new File(root + "/Nivezzle/" + name + ".jpg");


        editor.putString(name, String.valueOf(myDir));
        editor.commit();


        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
       // NetworkImageView thumbNail = (NetworkImageView) convertView
      //          .findViewById(R.id.thumbnail);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        final ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Movie m = movieItems.get(position);

        // thumbnail image
        link = m.getThumbnailUrl();
      /*  Picasso.with(activity)
                .load(m.getThumbnailUrl())
                .into(thumbNail);*/
       // thumbNail.setDefaultImageResId(R.mipmap.launcher_icon_1);
     //  thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // rating
        rating.setText("Points: " + String.valueOf(m.getRating()));

        // genre

        // release year
        year.setText(String.valueOf(m.getYear()));

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                try {

                    if (!myinitialDir.exists()) {
                        myinitialDir.mkdirs();
                    }

                    FileOutputStream out = new FileOutputStream(myDir);
                    path = myDir.toString();
                    Log.d(tag, "path " + path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    Log.d(tag, "inside failed" + e.toString());
                }
                finally
                {
                    bitmap = BitmapFactory.decodeFile(path);
                    thumbNail.setImageBitmap(bitmap);
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(tag, "inside failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };



        if(!isNetworkEnabled())
        {
            String pathFromShared =  sharedPreferences.getString(name,null);
            Log.d(tag, pathFromShared);
            Bitmap bitmap = BitmapFactory.decodeFile(pathFromShared);
            Log.d(tag,"inside no network");
            thumbNail.setImageBitmap(bitmap);
        }
        else
        {
            Picasso.with(activity)
                    .load(link)
                    .into(target);
        }

        return convertView;
    }

    private boolean isNetworkEnabled() {

        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkEnabled = activeNetwork !=null && activeNetwork.isConnectedOrConnecting();
        return isNetworkEnabled;
    }

}