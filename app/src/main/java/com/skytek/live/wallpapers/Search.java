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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.skytek.live.wallpapers.Adapters.Adapter;
import com.skytek.live.wallpapers.ModelClasses.ModelClass;
import com.skytek.live.wallpapers.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
private ImageView back_arrowob;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Context context;
    private ImageView loadingob;
    private LinearLayout loading_layoutob,text_layoutob;
    public RelativeLayout main_layoutob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = Search.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.foreground_app));
        }

        back_arrowob=findViewById(R.id.back_arrow);
        recyclerView=findViewById(R.id.search_recycler);
        loadingob=findViewById(R.id.loading);
        loading_layoutob=findViewById(R.id.loading_layout);
        main_layoutob=findViewById(R.id.main_layout);
        text_layoutob=findViewById(R.id.text_layout);

        recyclerView.setLayoutManager(new GridLayoutManager(Search.this, 2));


        Intent intent = getIntent();
        String data=intent.getStringExtra("data");

        Utilities utilities=new Utilities();
        if (!utilities.isOnline(Search.this)) {
            Snackbar snackbar = Snackbar.make((View) main_layoutob, "No Internet Connection", 1000);

            snackbar.getView().setBackgroundColor(-65536);
            snackbar.show();
        }
        else {
            loading_layoutob.setVisibility(View.VISIBLE);
            Glide.with(Search.this).load(R.drawable.icon).into(loadingob);

            data(data);
        }
        back_arrowob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        text_layoutob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



//





    }


    public void data(String text) {
        String mJSONURLString = "http://mobipixels.net//3d-Live-wallpapers-api/lwp-search.php?search="+text;

        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(Search.this);

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
                            loading_layoutob.setVisibility(View.GONE);
                            adapter  = new Adapter(dataset,context,R.layout.catalog_cell);
                            recyclerView.setAdapter(adapter);



                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                            text_layoutob.setVisibility(View.VISIBLE);
                        loading_layoutob.setVisibility(View.GONE);

                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);



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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Adapter.pDialog != null)
        {
            Adapter.pDialog.dismiss();
        }

    }
}