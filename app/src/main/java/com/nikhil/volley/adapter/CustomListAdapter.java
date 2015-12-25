package com.nikhil.volley.adapter;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.androidworks.nikhil.volleytest.MainActivity;
import com.androidworks.nikhil.volleytest.R;
import com.nikhil.volley.app.AppController;
import com.nikhil.volley.model.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
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

       // Log.d(tag,"inside adpter");
        sharedPreferences = activity.getSharedPreferences("linksPath", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        prefForFirstRun = activity.getSharedPreferences("firstRun", Context.MODE_PRIVATE);
        boolean firstRun = prefForFirstRun.getBoolean("initialRun", true);
        boolean secondRun = prefForFirstRun.getBoolean("secondRun", true);
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        //name = "Nivezzle" + String.valueOf(position);
        final File myinitialDir = new File(root + "/Nivezzle/");




        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);


        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
      //  final ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        Movie m = movieItems.get(position);

        // thumbnail image
        link = m.getThumbnailUrl();

       // ContextWrapper cw = new ContextWrapper(activity);
      //  File directory = cw.getDir("Nivezzle", Context.MODE_PRIVATE);

      //  name = "/Nivezzle/" + m.getTitle() + ".jpg";
     //   final File myPath=new File(directory,name);

        final  File myDir = new File(root + "/Nivezzle/" + m.getTitle() + ".jpg");
        //File file = new File(activity.getFilesDir(), filename);
        editor.putString(name, String.valueOf(myDir));
        editor.apply();


        ImageRequest ir = new ImageRequest(link, new Response.Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap response) {

                try {

                    if (!myinitialDir.exists()) {
                        myinitialDir.mkdirs();
                    }

                    FileOutputStream out = new FileOutputStream(myDir);
                   // path = myDir.toString();
                    // Log.d(tag, "path " + path);
                    response.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    //  Log.d(tag, "inside failed" + e.toString());
                }
            }
        }, 0, 0, null, null);

       // AppController.getInstance().addToRequestQueue(ir);
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());

        // rating
        rating.setText("Points: " + String.valueOf(m.getRating()));

        // genre

        // release year
        year.setText(String.valueOf(m.getYear()));

       /* Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                try {

                    if (!myinitialDir.exists()) {
                        myinitialDir.mkdirs();
                    }

                    FileOutputStream out = new FileOutputStream(myDir);
                    path = myDir.toString();
                   // Log.d(tag, "path " + path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                  //  Log.d(tag, "inside failed" + e.toString());
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
*/

           // Log.d(tag, "first run" + String.valueOf(firstRun));

      /*  if(!isNetworkEnabled() && !firstRun)
        {
            String pathFromShared =  sharedPreferences.getString(name,null);
           // Log.d(tag, pathFromShared);
            Bitmap bitmap = BitmapFactory.decodeFile(pathFromShared);

            thumbNail.setImageBitmap(bitmap);
        }

        else if(isNetworkEnabled() && secondRun)
        {
            String pathFromShared =  sharedPreferences.getString(name,null);
           // Log.d(tag, pathFromShared);
            Bitmap bitmap = BitmapFactory.decodeFile(pathFromShared);

            thumbNail.setImageBitmap(bitmap);
        }
        else*/

          /*  Picasso.with(activity).setIndicatorsEnabled(true);
            Picasso.with(activity)
                    .load(link)
                    .into(target);
        Picasso.with(activity)
                .load(link)
                .into(thumbNail);*/

        return convertView;
    }

    private boolean isNetworkEnabled() {

        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkEnabled = activeNetwork !=null && activeNetwork.isConnectedOrConnecting();
        return isNetworkEnabled;
    }

}