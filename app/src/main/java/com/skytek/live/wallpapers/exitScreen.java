package com.skytek.live.wallpapers;

import static com.skytek.live.wallpapers.MainLauncher.adcheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class exitScreen extends AppCompatActivity {
    Button yesob, noob;
    RelativeLayout exitlayoutob;
    Toolbar toolbar;
    ImageView backbutton;

    AdsManager adsManager;
    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_screen);
        yesob = findViewById(R.id.yes);
        noob = findViewById(R.id.no);
        exitlayoutob = findViewById(R.id.exit_layout);
        backbutton = findViewById(R.id.back_arrow);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

       // reviewManager = ReviewManagerFactory.create(this);

        initInAppReview();

        //heckforreview();

        adsManager = new AdsManager();

        adsManager.loadNativeAdd(exitScreen.this, R.id.fl_adplaceholder);


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

//    public void showRateApp() {
//        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // We can get the ReviewInfo object
//                ReviewInfo reviewInfo = task.getResult();
//
//                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
//                flow.addOnCompleteListener(task1 -> {
//                    // The flow has finished. The API does not indicate whether the user
//                    // reviewed or not, or even whether the review dialog was shown. Thus, no
//                    // matter the result, we continue our app flow.
//                });
//            }
//        });
//    }

//    public void checkforreview() {
//        Handler handler = new Handler();
//
//        final Runnable r = new Runnable() {
//            public void run() {
//
//                if (!adcheck) {
//                    showRateApp();
//                } else {
//                    checkforreview();
//                }
//            }
//        };
//
//        handler.postDelayed(r, 100);
//    }

}