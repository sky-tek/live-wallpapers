package com.skytek.live.wallpapers;



import static com.skytek.live.wallpapers.Fragments.Home.check_more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.skytek.live.wallpapers.Adapters.ParentItemAdapter;
import com.skytek.live.wallpapers.ModelClasses.ModelClass;
import com.skytek.live.wallpapers.ModelClasses.ParentItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class exitScreen extends AppCompatActivity {
    Button yesob, noob;
    RelativeLayout exitlayoutob;
    Toolbar toolbar;
    ImageView backbutton;

    AdsManager adsManager;
    private ReviewManager reviewManager;

    private RecyclerView ParentRecyclerViewItem;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_screen);

        data();
        yesob = findViewById(R.id.yes);
        noob = findViewById(R.id.no);
        exitlayoutob = findViewById(R.id.exit_layout);
        backbutton = findViewById(R.id.back_arrow);
        ParentRecyclerViewItem = findViewById(R.id.ParentRecyclerViewItem);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

       // reviewManager = ReviewManagerFactory.create(this);

        initInAppReview();

        //heckforreview();

        adsManager = new AdsManager();




        yesob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        noob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

    private void initInAppReview() {
        Log.d("adasdasdasdasdasdasd", "initInAppReview: called!");
        Log.d("TAG", "initInAppReview: called!");
        reviewManager = ReviewManagerFactory.create(this);
        showRateApp();

    }
    public void showRateApp() {
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "showRateApp: task successful");
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                // Launch the in-app review flow
                Task<Void> flow = reviewManager.launchReviewFlow((Activity) this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
//                showRateAppFallbackDialog();
                Log.d("TAG", "showRateApp: task not successful");
            }
        });
    }

    @Override
    protected void onResume() {

        getWindow().setStatusBarColor(Color.parseColor("#181725"));
        super.onResume();
    }

    public void data() {

        final String JSON_URL = "https://mobipixels.net/3d-Live-wallpapers-api/newt.php";
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // Initialize a new JsonArrayRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response != null) {
                                adsManager.loadNativeAdd(exitScreen.this, R.id.fl_adplaceholder);
                                try {
                                    //getting the whole json object from the response
                                    JSONArray jsonArray = response.getJSONArray("response");

                                    JSONObject catObject;
                                    String catName;
                                    String catid;

                                    ArrayList<ParentItem> dataset1 = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ArrayList<ModelClass> dataset = new ArrayList<>();
                                        catObject = jsonArray.getJSONObject(i);
                                        catName = jsonArray.getJSONObject(i).getString("Category");
                                        if(catName.equals("Trending") || catName.equals("New"))
                                        {
                                            Log.d("fdsfd", "onResponse: "+catName);
                                            catid = jsonArray.getJSONObject(i).getString("id");

                                            JSONArray wallpapers = catObject.getJSONArray("wallpapers");
                                            JSONObject wallpaper;

                                            for (int j = 0; j < wallpapers.length(); j++) {
                                                if(j < 3 )
                                                {
                                                    wallpaper = wallpapers.getJSONObject(j);
                                                    String thumb_path = wallpaper.getString("thumbPath");
                                                    String id = wallpaper.getString("id");
                                                    String likes = wallpaper.getString("likes");
                                                    String downloads = wallpaper.getString("Downloads");
                                                    dataset.add(new ModelClass(thumb_path, id, likes, downloads));
                                                }

                                            }
                                            dataset1.add(new ParentItem(catid, catName, dataset));
                                        }
                                        else {
                                            Log.d("checkCatname" , "cat name is "+catName);
                                        }
                                    }
                                    check_more=true;
                                    ParentItemAdapter parentItemAdapter = new ParentItemAdapter(dataset1, exitScreen.this);
                                    ParentRecyclerViewItem.setAdapter(parentItemAdapter);


                                    LinearLayoutManager layoutManager = new LinearLayoutManager(exitScreen.this);
                                    ParentRecyclerViewItem.setLayoutManager(layoutManager);

//                                    if (mBundleRecyclerViewState != null) {
//                                        new Handler().postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                mListState = mBundleRecyclerViewState.getParcelable("KEY_RECYCLER_STATE");
//                                                ParentRecyclerViewItem.getLayoutManager().onRestoreInstanceState(mListState);
//                                            }
//                                        }, 50);
//                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(exitScreen.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
            });


            //adding the string request to request queue
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}