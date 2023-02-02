package com.skytek.live.wallpapers;

import static com.skytek.live.wallpapers.Adapters.Adapter.REQUEST_FOR_ACTIVITY_CODE;
import static com.skytek.live.wallpapers.Adapters.Adapter.id1;
import static com.skytek.live.wallpapers.Adapters.Adapter.idnew;
import static com.skytek.live.wallpapers.Adapters.Adapter.idold;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.material.snackbar.Snackbar;
import com.skytek.live.wallpapers.Adapters.Adapter;
import com.skytek.live.wallpapers.ModelClasses.ModelClass;
import com.skytek.live.wallpapers.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class categorythumnail extends AppCompatActivity {
    public static String category;


    public static RelativeLayout categorymainob;

private RecyclerView recyclerView;
   private Adapter adapter;
  private Context context;




    Snackbar snackbar;
    public static AdsManager adsManager;
//    private Bundle mBundleRecyclerViewState;
//    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorythumnail);
//        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).permitAll().build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_app));
        }

        context = categorythumnail.this;

        categorymainob = findViewById(R.id.categorymain);
        recyclerView = findViewById(R.id.recycler);
        category = getIntent().getStringExtra("category");

        adsManager = new AdsManager();
        adsManager.loadBanner(categorythumnail.this, R.id.bannercontainer);

        recyclerView.setLayoutManager(new GridLayoutManager(categorythumnail.this, 2));
        Utilities utilities=new Utilities();
        if (!utilities.isOnline(categorythumnail.this)) {
            Snackbar snackbar = Snackbar.make((View) this.categorymainob, "No Internet Connection", 1000);
            this.snackbar = snackbar;
            snackbar.getView().setBackgroundColor(-65536);
            this.snackbar.show();
        }
        else {
            data();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                idold=id1;
                Toast.makeText(getApplicationContext(), "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
            } else {
                idnew=idold;

            }

        }
    }




    //    @Override
//    protected void onPause() {
//        mBundleRecyclerViewState = new Bundle();
//
//
//           if (recyclerView.getLayoutManager()!=null) {
//               mListState = recyclerView.getLayoutManager().onSaveInstanceState();
//               mBundleRecyclerViewState.putParcelable("KEY_RECYCLER_STATE", mListState);
//           }
//        super.onPause();
//    }




    public void data() {
        String mJSONURLString = "https://mobipixels.net/3d-Live-wallpapers-api/live_wall_single_cat.php?id="+category+"&sort=1";

        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(categorythumnail.this);

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                mJSONURLString,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<ModelClass> dataset = new ArrayList<ModelClass>();
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject student = response.getJSONObject(i);

                                // Get the current student (json object) data
                                String id = student.getString("id");
                                String thumb_path = student.getString("thumb_path");
                                String likes = student.getString("likes");
                                String downloads = student.getString("downloads");

                                dataset.add(new ModelClass(thumb_path,id,likes,downloads));


                                Log.e("gdsgdsgds", "name"+thumb_path);

                                // Display the formatted json data in text view
                            }
                            adapter  = new Adapter(dataset,context,R.layout.catalog_cell);
                            recyclerView.setAdapter(adapter);



//                            if (mBundleRecyclerViewState != null) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mListState = mBundleRecyclerViewState.getParcelable("KEY_RECYCLER_STATE");
//                                        recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
//                                    }
//                                }, 50);
//
//                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(categorythumnail.this," No Internet", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);



    }
}