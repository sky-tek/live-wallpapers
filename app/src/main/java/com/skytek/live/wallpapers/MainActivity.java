package com.skytek.live.wallpapers;

import static android.widget.Toast.LENGTH_SHORT;
import static com.skytek.live.wallpapers.Adapters.Adapter.REQUEST_FOR_ACTIVITY_CODE;
import static com.skytek.live.wallpapers.Adapters.Adapter.idnew;
import static com.skytek.live.wallpapers.Adapters.Adapter.idold;
import static com.skytek.live.wallpapers.Adapters.Adapter.id1;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.skytek.live.wallpapers.Adapters.Adapter;
import com.skytek.live.wallpapers.Adapters.CustomAdapter;
import com.skytek.live.wallpapers.Adapters.ViewPagerAdapter;
import com.skytek.live.wallpapers.Fragments.Categories;
import com.skytek.live.wallpapers.Fragments.Home;
import com.skytek.live.wallpapers.ModelClasses.datamodel;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    Toolbar toolbarob;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    AdsManager adsManager;








    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setStatusBarColor(Color.parseColor("#181725"));
        adsManager = new AdsManager();
        adsManager.load_inter(this);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder()).permitAll().build());
        context = MainActivity.this;
        toolbarob = findViewById(R.id.toolbar);
        setSupportActionBar(toolbarob);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = findViewById(R.id.viewpager);

        viewPager.setOffscreenPageLimit(1);
        // setting up the adapter
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // add the fragments




        viewPagerAdapter.add(new Home(), "Home");
        viewPagerAdapter.add(new Categories(), "Categories");
        // Set the adapter
        viewPager.setAdapter(viewPagerAdapter);

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //app update manager



    }



    //app update ends
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        SearchView searchView=(SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {



                    Intent intent=new Intent(MainActivity.this,Search.class);
                    intent.putExtra("data",searchView.getQuery().toString());
                    startActivity(intent);
                

                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        try {
//            Bundle params = new Bundle();
//            params.putString("Button_ID", "Total");
//            switch (item.getItemId()) {
//
//                case R.id.search:
//
//                    break;
//
//                default:
//                    return super.onOptionsItemSelected(item);
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                idold=id1;
                adsManager.show_inter(this);
                Toast.makeText(getApplicationContext(), "Wallpaper Set Successfully", LENGTH_SHORT).show();
            } else {
                idnew=idold;

            }


        }
    }



}