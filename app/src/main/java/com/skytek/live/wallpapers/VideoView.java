package com.skytek.live.wallpapers;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.falcon.video.downloader.Helper.AdsManager;

import java.io.File;
import java.io.IOException;

public class VideoView extends AppCompatActivity {
    CheckBox checkBoxob;
    android.widget.VideoView videoob;
    MediaPlayer mp1;
    Button setLauncherob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoob = findViewById(R.id.video);
        setLauncherob = findViewById(R.id.videobtn);
        checkBoxob = findViewById(R.id.checkbox);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(Color.parseColor("#1F212D"));


        videoob.setVideoPath(getExternalFilesDir("offline").getAbsolutePath() + "/" + MainLauncher.a);
        this.videoob.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer param1MediaPlayer) {
                param1MediaPlayer.setLooping(true);
                mp1 = param1MediaPlayer;
            }
        });
        checkBoxob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {

                try
                {
                    if (VideoView.this.checkBoxob.isChecked()) {
                        mp1.setVolume(1.0F, 1.0F);

                    } else {
                        VideoView.this.mp1.setVolume(0.0F, 0.0F);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });
        setLauncherob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (checkBoxob.isChecked()) {

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("touch " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("touch /data/data/com.skytek.live.wallpapers/files/unmute");
                        }
//                        VideoLiveWallpaperoffline.unmuteMusic(VideoView.this);
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }

                } else {

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Runtime.getRuntime().exec("rm " + getFilesDir().toPath() + "/unmute");
                        } else {
                            Runtime.getRuntime().exec("rm /data/data/com.skytek.live.wallpapers/files/unmute");
                        }
//                        VideoLiveWallpaperoffline.muteMusic(VideoView.this);
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                }

                try {
                    final Intent intent1 = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent1.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(VideoView.this, VideoLiveWallpaperoffline.class));
                    startActivityForResult(intent1, 100);
                }
                catch (Exception e)
                {
                    Log.d("checkexcpetion" , "exception is "+e);
                }


//                // There is a problem, that after you choose "Set to desktop (and lock screen)",
//                // the preview activity does not back to MainActivity directly,
//                // instead, it backs to desktop and then back to MainActivity (very quick, invisible),
//                // and in this time GLWallpaperService starts with no LWApplication.currentWallpaperCard,
//                // no record in SharedPreference if you run app for the first time.
//                // To solve this, we let it fallback to internal wallpaper when it gets null from record,
//                // and because it will be back to MainActivity very quick,
//                // currentWallpaperCard will be set after that, and you'll see it next desktop appearing.
//                startActivityForResult(intent, PREVIEW_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
//                SharedPreferences.Editor editor = getSharedPreferences("values", MODE_PRIVATE).edit();
//                editor.putString("name", name);
//                editor.apply();
                Toast.makeText(getApplicationContext(), "Set Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onResume() {
        if (!checkBoxob.isChecked())
            checkBoxob.toggle();
        videoob.start();
        super.onResume();
    }

}