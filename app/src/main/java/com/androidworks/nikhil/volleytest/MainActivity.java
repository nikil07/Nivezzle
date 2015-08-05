package com.androidworks.nikhil.volleytest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nikhil.volley.adapter.CustomListAdapter;
import com.nikhil.volley.app.AppController;
import com.nikhil.volley.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    // Log tag
    private static final String TAG = "Nikhil";
    // private static final String url = "http://api.androidhive.info/json/movies.json";
    private static final String url = "http://1-dot-jsondatanivezzle.appspot.com/actualnivezzle";
    public static List<String> imageURLS = new ArrayList<>();
    public static List<String> answerKeys = new ArrayList<>();
    SharedPreferences prefForFirstRun;
    String JSONData = "";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomListAdapter adapter;

    static public List<String> sendImageURLList() {
        return imageURLS;
    }

    static public List<String> sendAnswerKeys() {
        return answerKeys;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);


        adapter = new CustomListAdapter(this, movieList);

        listView.setAdapter(adapter);

        prefForFirstRun = getSharedPreferences("firstRun",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorForFirstRun = prefForFirstRun.edit();

        if(prefForFirstRun.getBoolean("initialRun",true)) {
            editorForFirstRun.putBoolean("initialRun", true);
            editorForFirstRun.commit();
        }
        else
        {
            editorForFirstRun.putBoolean("initialRun", false);
            editorForFirstRun.commit();
        }


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
                                        boolean onBackPressed = sharedPreferences.getBoolean("onBackPressed", false);
                                        boolean firstRun = prefForFirstRun.getBoolean("initialRun",true);
                                        Log.d(TAG,String.valueOf(firstRun));
                                        if(firstRun)
                                        {
                                            fetchNewData();
                                            editorForFirstRun.putBoolean("initialRun",false);
                                            editorForFirstRun.commit();
                                            Log.d(TAG,"inside first run");
                                        }
                                        else
                                        {
                                            if(onBackPressed)
                                                fetchData();
                                            else
                                                fetchData();
                                        }

                                    }
                                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                sendImageURLList();
                sendAnswerKeys();

                Intent intent = new Intent(MainActivity.this, MainAnswerScreenActivity.class);
                //  Log.d(TAG,String.valueOf(position));
                intent.putExtra("rowValue", position);
                startActivity(intent);

            }
        });

    }

    // override for swipe refresh interface
    @Override
    public void onRefresh() {
        Snackbar.make(swipeRefreshLayout,"Fetching new puzzles if available",Snackbar.LENGTH_SHORT).show();
        fetchNewData();
    }

    private void fetchData() {

        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
        String JSONData = sharedPreferences.getString("JSONData", null);

        try {
            JSONObject jsonObject = new JSONObject(JSONData);
            JSONArray response = jsonObject.getJSONArray("NivezzleJSON");

            movieList.clear();
            if (imageURLS.size() > 1) {
                imageURLS.clear();
                answerKeys.clear();
            }
            // Parsing json
            for (int i = 0; i < response.length(); i++) {
                try {

                    JSONObject obj = response.getJSONObject(i);
                    Movie movie = new Movie();
                    movie.setTitle(obj.getString("title"));
                    movie.setThumbnailUrl(obj.getString("image"));
                    imageURLS.add(obj.getString("image"));
                    movie.setRating(((Number) obj.get("points"))
                            .intValue());
                    movie.setYear(obj.getString("difficulty"));
                    answerKeys.add(obj.getString("answer"));


                    // adding movie to movies array
                    movieList.add(movie);
                    //   Log.d(TAG,movie.getTitle());

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            //    scaleInAnimationAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();

            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {

        }

    }

    public void fetchNewData() {
        // showing refresh animation before making http call

        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        movieList.clear();
        if (imageURLS.size() > 1) {
            imageURLS.clear();
            answerKeys.clear();
        }

        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject bigResponse) {
                        //  Log.d(TAG, response.toString());
                        editor.putString("JSONData", String.valueOf(bigResponse));
                        editor.commit();
                        JSONArray response = new JSONArray();
                        try {
                            response = bigResponse.getJSONArray("NivezzleJSON");
                        } catch (JSONException e) {

                        }
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                imageURLS.add(obj.getString("image"));
                                movie.setRating(((Number) obj.get("points"))
                                        .intValue());
                                movie.setYear(obj.getString("difficulty"));
                                answerKeys.add(obj.getString("answer"));


                                // adding movie to movies array
                                movieList.add(movie);
                                //   Log.d(TAG,movie.getTitle());

                            } catch (JSONException e) {
                                Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        //    scaleInAnimationAdapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
                        JSONData = response.toString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                //  Log.d(TAG, "Error: " + error.getMessage());

                // stopping swipe refresh

                swipeRefreshLayout.setRefreshing(false);

            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings BRO", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("onBackPressed", true);
        editor.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("onBackPressed", true);
        editor.commit();
    }
}