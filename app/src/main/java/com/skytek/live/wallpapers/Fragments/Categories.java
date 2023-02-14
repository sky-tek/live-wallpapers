package com.skytek.live.wallpapers.Fragments;

import static com.skytek.live.wallpapers.Fragments.Home.check_more;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.skytek.live.wallpapers.Adapters.ParentItemAdapter;
import com.skytek.live.wallpapers.MainActivity;
import com.skytek.live.wallpapers.ModelClasses.ModelClass;
import com.skytek.live.wallpapers.ModelClasses.ParentItem;
import com.skytek.live.wallpapers.R;
import com.skytek.live.wallpapers.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categories extends Fragment {
    View view;
    private ShimmerFrameLayout mShimmerViewContainer;
    private RecyclerView ParentRecyclerViewItem;

//    private Bundle mBundleRecyclerViewState;
//    private Parcelable mListState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_categories,container,false);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        ParentRecyclerViewItem = view.findViewById(R.id.parent_recyclerview_cat);
        Utilities utilities=new Utilities();
        if (!utilities.isOnline(getActivity())) {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "No Internet Connection", 1000);

            snackbar.getView().setBackgroundColor(-65536);
            snackbar.show();
        }
        else {
            mShimmerViewContainer.startShimmerAnimation();
            data();
        }

        return view;
    }

//
//    @Override
//    public void onPause() {
//        mBundleRecyclerViewState = new Bundle();
//        if (ParentRecyclerViewItem.getLayoutManager() != null) {
//            mListState = ParentRecyclerViewItem.getLayoutManager().onSaveInstanceState();
//            mBundleRecyclerViewState.putParcelable("KEY_RECYCLER_STATE", mListState);
//        }
//        super.onPause();
//    }

    public void data() {

        final String JSON_URL = "https://mobipixels.net/3d-Live-wallpapers-api/new.php";
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            // Initialize a new JsonArrayRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    JSON_URL,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (response != null) {
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

                                        catid = jsonArray.getJSONObject(i).getString("id");

                                        JSONArray wallpapers = catObject.getJSONArray("Wallpapers");
                                        JSONObject wallpaper;

                                        for (int j = 0; j < wallpapers.length() ; j++) {
                                            Log.d("checkcontentvolly" , "volly content is "+wallpapers.length());
                                            if(j < 5)
                                            {
                                                wallpaper = wallpapers.getJSONObject(j);
                                                String thumb_path = wallpaper.getString("thumbPath");
                                                String id = wallpaper.getString("id");
                                                String likes = wallpaper.getString("likes");
                                                String downloads = wallpaper.getString("downloads");
                                                dataset.add(new ModelClass(thumb_path, id, likes, downloads));
                                            }
                                            else {
                                                break;
                                            }
                                        }
                                        dataset1.add(new ParentItem(catid, catName, dataset));
                                    }
                                    check_more=false;
                                    ParentItemAdapter parentItemAdapter = new ParentItemAdapter(dataset1, getActivity());
                                    ParentRecyclerViewItem.setAdapter(parentItemAdapter);

                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);


                                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                    ParentRecyclerViewItem.setLayoutManager(layoutManager);
//
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
                    Toast.makeText(MainActivity.context, "No Internet", Toast.LENGTH_SHORT).show();
                }
            });


            //adding the string request to request queue
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}