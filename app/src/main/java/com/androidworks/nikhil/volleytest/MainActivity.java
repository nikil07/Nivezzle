package com.androidworks.nikhil.volleytest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.*;
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
    public static List<String> imageURLS = new ArrayList<>();
    public static List<String> answerKeys = new ArrayList<>();

    // private static final String url = "http://api.androidhive.info/json/movies.json";
    private static final String url = "http://1-dot-jsondatanivezzle.appspot.com/actualnivezzle";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);


        adapter = new CustomListAdapter(this, movieList);
        //animationAdapter = new AlphaInAnimationAdapter(adapter);
       // scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
       // scaleInAnimationAdapter.setAbsListView((listView));
        listView.setAdapter(adapter);



        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request1
        pDialog.setMessage("Loading...");
        //pDialog.show();

        // Creating volley request obj

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        movieList.clear();
                                        fetchData();
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



    @Override
    public void onRefresh() {
        fetchData();
    }


    public void fetchData() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        movieList.clear();
        if (imageURLS.size() > 1) {
            imageURLS.clear();
            answerKeys.clear();
        }

        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //  Log.d(TAG, response.toString());
                        hidePDialog();

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
                                // Genre is json array

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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                //  Log.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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

    static public List<String> sendImageURLList() {
        return imageURLS;
    }

    static public List<String> sendAnswerKeys() {
        return answerKeys;
    }
}