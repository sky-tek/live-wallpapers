package com.skytek.live.wallpapers;

import static android.content.ContentValues.TAG;

import static com.skytek.live.wallpapers.MyApplication.appOpenManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

public class splashMainActivity extends AppCompatActivity {
ImageView splashob;
TextView text;
Button start;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_main);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(Color.parseColor("#181725"));
        splashob=findViewById(R.id.splash);
        text=findViewById(R.id.textView2);
        start=findViewById(R.id.start);
//        AdsManager adsManager = new AdsManager();
//        adsManager.load_inter(splashMainActivity.this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        final ActivityManager activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE
        );
        ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
        Log.d("dsgdsgds", "onCreate: "+configInfo);
        Glide.with(splashMainActivity.this).load(R.drawable.icon).into(splashob);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!appOpenManager.isShowingAd && appOpenManager.isAdAvailable()){
                    appOpenManager.showAdIfAvailable();
                }
            }
        }, 3000);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                text.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);

            }
        }, 4000);
start.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(splashMainActivity.this, MainLauncher.class));
//        Bundle bundle = new Bundle();
//        bundle.putString("splash","splashtotal");
//        mFirebaseAnalytics.logEvent("splashtotal",bundle);
//
//        if (adsManager.show_inter(splashMainActivity.this)){
//            finish();
//            Bundle bundle1 = new Bundle();
//            bundle1.putString("splash","splashShowed");
//            mFirebaseAnalytics.logEvent("splashShowed",bundle1);
//        }
//        else {
            finish();
//        }
    }
});
    }


}