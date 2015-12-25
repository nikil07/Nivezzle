package com.androidworks.nikhil.volleytest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
import com.google.example.games.basegameutils.BaseGameActivity;
import com.nikhil.volley.adapter.CustomListAdapter;
import com.nikhil.volley.app.AppController;
import com.nikhil.volley.dbutils.DBAdapter;
import com.nikhil.volley.dbutils.DBHelper;
import com.nikhil.volley.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseGameActivity implements SwipeRefreshLayout.OnRefreshListener {
    // Log tag
    private static final String TAG = "Nikhil";
    // private static final String url = "http://api.androidhive.info/json/movies.json";
    private static final String url = "http://1-dot-jsondatanivezzle.appspot.com/actualnivezzle";
    public static List<String> imageURLS = new ArrayList<>();
    public static List<String> answerKeys = new ArrayList<>();
    public static List<Integer> pointsList = new ArrayList<>();
    SharedPreferences prefForFirstRun;
    String JSONData = "", difficulty = "";
    DBAdapter adapter_ob;
    DBHelper helper_ob;
    SQLiteDatabase db_ob;
    int count=0;
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomListAdapter adapter;

    static public List<String> sendImageURLList() {
        return imageURLS;
    }

    static public List<String> sendAnswerKeys() {
        return answerKeys;
    }
    static public List<Integer> sendPoints() {
        return pointsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      ListView listView = (ListView) findViewById(R.id.list);

        Bundle bundle = getIntent().getExtras();
        difficulty = bundle.getString("difficulty");


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        prefForFirstRun = getSharedPreferences("firstRun",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorForFirstRun = prefForFirstRun.edit();
        editorForFirstRun.putBoolean("secondRun",false);
        if(prefForFirstRun.getBoolean("initialRun",true)) {
            editorForFirstRun.putBoolean("initialRun", true);
            editorForFirstRun.apply();
        }
        else
        {
            editorForFirstRun.putBoolean("initialRun", false);
            editorForFirstRun.apply();
        }
        if(count>0)
        {
            editorForFirstRun.putBoolean("secondRun",true);
        }
       // Log.d(TAG, movieList.toString());
        adapter = new CustomListAdapter(this, movieList);

        listView.setAdapter(adapter);
        boolean firstRun_2 = prefForFirstRun.getBoolean("initialRun",true);
       // Log.d(TAG, "first run variable" + String.valueOf(firstRun_2));

        swipeRefreshLayout.setOnRefreshListener(this);
        if(Build.VERSION.SDK_INT >= 14) {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }
        else
        {
            swipeRefreshLayout.setColorSchemeColors(8172354,48340,16485376,16007990);
        }

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
                                        boolean onBackPressed = sharedPreferences.getBoolean("onBackPressed", false);
                                        boolean firstRun = prefForFirstRun.getBoolean("initialRun",true);
                                      ///  Log.d(TAG,String.valueOf(firstRun));
                                        if(firstRun)
                                        {
                                            fetchNewData();
                                            count =1 ;
                                            editorForFirstRun.putBoolean("initialRun", false);
                                            editorForFirstRun.apply();
                                          //  Log.d(TAG,"inside first run");
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
                sendPoints();

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
                pointsList.clear();
            }
            // Parsing json
            for (int i = 0; i < response.length(); i++) {
                try {

                    JSONObject obj = response.getJSONObject(i);
                    if(obj.getString("difficulty").equalsIgnoreCase(difficulty)) {
                        Movie movie = new Movie();
                        movie.setTitle(obj.getString("title"));
                        movie.setThumbnailUrl(obj.getString("image"));
                        imageURLS.add(obj.getString("image"));
                        movie.setRating(((Number) obj.get("points"))
                                .intValue());
                        movie.setYear(obj.getString("difficulty"));
                        answerKeys.add(obj.getString("answer"));
                        pointsList.add(Integer.valueOf(obj.getString("points")));

                        // adding movie to movies array
                        movieList.add(movie);
                        //   Log.d(TAG,movie.getTitle());
                    }
                } catch (JSONException e) {
                   // Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                    Snackbar.make(swipeRefreshLayout,"Make sure you have a working internet connection",Snackbar.LENGTH_SHORT).show();
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
            Snackbar.make(swipeRefreshLayout,"Please swipe down to refresh the list",Snackbar.LENGTH_LONG).show();
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

        JsonObjectRequest movieReq = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject bigResponse) {
                        //  Log.d(TAG, response.toString());
                        editor.putString("JSONData", String.valueOf(bigResponse));
                        editor.apply();
                        JSONArray response = new JSONArray();
                        try {
                            response = bigResponse.getJSONArray("NivezzleJSON");
                        } catch (JSONException e) {

                        }
                        // Parsing json
                        //JSONObject cluesObject = new JSONObject();;
                        JSONObject initialCluesObject = new JSONObject();
                        Log.d(TAG,String.valueOf(response.length()));
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                if (obj.getString("difficulty").equalsIgnoreCase(difficulty)) {
                                    Movie movie = new Movie();
                                    movie.setTitle(obj.getString("title"));
                                    movie.setThumbnailUrl(obj.getString("image"));
                                    imageURLS.add(obj.getString("image"));
                                    movie.setRating(((Number) obj.get("points"))
                                            .intValue());
                                    movie.setYear(obj.getString("difficulty"));
                                    //Log.d(TAG, obj.getString("difficulty"));
                                    answerKeys.add(obj.getString("answer"));
                                    pointsList.add(Integer.valueOf(obj.getString("points")));

                                    // adding movie to movies array
                                    movieList.add(movie);

                                    //   Log.d(TAG,movie.getTitle());

                                   // JSONArray cluesArray =  new JSONArray();


                                    JSONArray clues = obj.getJSONArray("clues");
                                    Log.d(TAG, clues.toString());
                                    /*for(int j=0;j<clues.length();j++)
                                    {
                                        Log.d(TAG,clues.getString(j));

                                    }*/

                                    initialCluesObject.put(obj.getString("answer"), clues);


                                }
                                }catch(JSONException e){
                                   // Toast.makeText(MainActivity.this, "Make sure you have a working internet connection", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                }

                        }
                        // adding the clues stuff to main JSON object


                        Log.d(TAG,"clues JSON Array"+initialCluesObject.toString());
                        editor.putString("cluesJson", initialCluesObject.toString());
                        editor.apply();


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
               // Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            //      Log.d(TAG, "Error: " + error.getMessage());

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
        helper_ob = new DBHelper(this,null,null,1);
        db_ob = helper_ob.getWritableDatabase();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings BRO", Toast.LENGTH_SHORT).show();
            helper_ob.onUpgrade(db_ob,1,1);
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
        editor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("JSONPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("onBackPressed", true);
        editor.apply();
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}