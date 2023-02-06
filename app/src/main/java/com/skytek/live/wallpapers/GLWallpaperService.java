/*
 * Copyright 2019 Alynx Zhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skytek.live.wallpapers;

import static com.skytek.live.wallpapers.Adapters.Adapter.id1;
import static com.skytek.live.wallpapers.Adapters.Adapter.idnew;
import static com.skytek.live.wallpapers.Adapters.Adapter.idold;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.skytek.live.wallpapers.Adapters.Adapter;

import java.io.IOException;

/**
 *
 * Generally, WallpaperService should not depend other parts of app.
 * Because sometimes (like phone restarted) service starts before Activity and Application.
 * So we cannot get data from them.
 *
 * To solve this, it's better to store WallpaperCard
 * into storage, SharedPreferences is better than JSON because it's easier to get data.
 *
 * So when we cannot get current WallpaperCard from LWApplication, we read SharedPreference,
 * then build a temp WallpaperCard (in fact only type and path matter for service, so we can
 * set thumbnail to null).
 *
 * And when we get a current WallpaperCard, we save it to SharedPreference for further loading.
 *
 */
public class GLWallpaperService extends WallpaperService {
    @SuppressWarnings("unused")
    private static final String TAG = "GLWallpaperService";

    class GLWallpaperEngine extends Engine {
        private static final String TAG = "GLWallpaperEngine";
        private final Context context;
        private GLWallpaperSurfaceView glSurfaceView = null;
        private SimpleExoPlayer exoPlayer = null;
        private MediaSource videoSource = null;
        private DefaultTrackSelector trackSelector = null;
        private GLWallpaperRenderer renderer = null;
        private boolean allowSlide = false;
        final private int videoRotation = 0;
        final private int videoWidth = 1080;
        final private int videoHeight = 1920;


        private class GLWallpaperSurfaceView extends GLSurfaceView {
            @SuppressWarnings("unused")
            private static final String TAG = "GLWallpaperSurface";

            public GLWallpaperSurfaceView(Context context) {
                super(context);
            }

            /**
             * This is a hack. Because Android Live Wallpaper only has a Surface.
             * So we create a GLSurfaceView, and when drawing to its Surface,
             * we replace it with WallpaperEngine's Surface.
             */
            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            void onDestroy() {
                super.onDetachedFromWindow();
            }
        }

        GLWallpaperEngine(@NonNull final Context context) {
            this.context = context;
            setTouchEventsEnabled(false);
        }

        // @Override
        // public void onTouchEvent(MotionEvent event) {
        //     super.onTouchEvent(event);
        // }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            createGLSurfaceView();
            int width = surfaceHolder.getSurfaceFrame().width();
            int height = surfaceHolder.getSurfaceFrame().height();
            renderer.setScreenSize(width, height);
            startPlayer();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (renderer != null) {
                if (visible) {
                    glSurfaceView.onResume();
                    startPlayer();
                } else {
                    stopPlayer();
                    glSurfaceView.onPause();
                    // Prevent useless renderer calculating.
                    allowSlide = false;
                }
            }
        }

        @Override
        public void onOffsetsChanged(
            float xOffset, float yOffset,
            float xOffsetStep, float yOffsetStep,
            int xPixelOffset, int yPixelOffset
        ) {
            super.onOffsetsChanged(
                xOffset, yOffset, xOffsetStep,
                yOffsetStep, xPixelOffset, yPixelOffset
            );
            if (allowSlide && !isPreview()) {
                renderer.setOffset(0.5f - xOffset, 0.5f - yOffset);
            }
        }

        @Override
        public void onSurfaceChanged(
            SurfaceHolder surfaceHolder, int format,
            int width, int height
        ) {
            super.onSurfaceChanged(surfaceHolder, format, width, height);
            renderer.setScreenSize(width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            stopPlayer();
            glSurfaceView.onDestroy();
        }

        private void createGLSurfaceView() {
            if (glSurfaceView != null) {
                glSurfaceView.onDestroy();
                glSurfaceView = null;
            }
            glSurfaceView = new GLWallpaperSurfaceView(context);
            final ActivityManager activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE
            );
            if (activityManager == null) {
                throw new RuntimeException("Cannot get ActivityManager");
            }
            final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
            if (configInfo.reqGlEsVersion >= 0x30000) {
                Utils.debug(TAG, "Support GLESv3");
                glSurfaceView.setEGLContextClientVersion(3);
                renderer = new GLES30WallpaperRenderer(context);
            } else if (configInfo.reqGlEsVersion >= 0x20000) {
                Utils.debug(TAG, "Fallback to GLESv2");
                glSurfaceView.setEGLContextClientVersion(2);
                renderer = new GLES20WallpaperRenderer(context);
            } else {
                throw new RuntimeException("Needs GLESv2 or higher");
            }
            glSurfaceView.setPreserveEGLContextOnPause(true);
            glSurfaceView.setRenderer(renderer);
            // On demand render will lead to black screen.
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }



        private void startPlayer() {
            if (exoPlayer != null) {
                stopPlayer();
            }
            if (isPreview()) {
                if (id1!=null) {
                    idnew = id1;
                }
            }
            else {
                if (idold!=null){
                    idnew=idold;

                }
            }
//            try {
//                getVideoMetadata();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            trackSelector = new DefaultTrackSelector();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            exoPlayer.setVolume(0.0f);
            // Disable audio decoder.
            final int count = exoPlayer.getRendererCount();
            for (int i = 0; i < count; ++i) {
                if (exoPlayer.getRendererType(i) == C.TRACK_TYPE_AUDIO) {
                    trackSelector.setParameters(
                        trackSelector.buildUponParameters().setRendererDisabled(i, true)
                    );
                }
            }
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            final DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "com.skytek.live.wallpapers")
            );
            // ExoPlayer can load file:///android_asset/ uri correctly.
            videoSource = new ExtractorMediaSource.Factory(
                dataSourceFactory
            ).createMediaSource(Uri.parse(getExternalFilesDir("live").getAbsolutePath() + "/"+ Adapter.idnew));
            // Let we assume video has correct info in metadata, or user should fix it.

            renderer.setVideoSizeAndRotation(videoWidth, videoHeight, videoRotation);
            // This must be set after getting video info.
            renderer.setSourcePlayer(exoPlayer);
            exoPlayer.prepare(videoSource);
            // ExoPlayer's video size changed listener is buggy. Don't use it.
            // It give's width and height after rotation, but did not rotate frames.

            exoPlayer.setPlayWhenReady(true);
        }
//        private void getVideoMetadata() throws IOException {
//            final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//
//                    mmr.setDataSource(context,Uri.parse(getExternalFilesDir("live").getAbsolutePath() + "/"+ Adapter.id1) );
//
//            final String rotation = mmr.extractMetadata(
//                    MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
//            );
//            final String width = mmr.extractMetadata(
//                    MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
//            );
//            final String height = mmr.extractMetadata(
//                    MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
//            );
//            mmr.release();
//            videoRotation = Integer.parseInt(rotation);
//            videoWidth = Integer.parseInt(width);
//            videoHeight = Integer.parseInt(height);
//        }
        private void stopPlayer() {
            if (exoPlayer != null) {
                if (exoPlayer.getPlayWhenReady()) {
                    Utils.debug(TAG, "Player stopping");
                    exoPlayer.setPlayWhenReady(false);
                    exoPlayer.stop();
                }
                exoPlayer.release();
                exoPlayer = null;
            }
            videoSource = null;
            trackSelector = null;
        }
    }

    @Override
    public Engine onCreateEngine() {
        return new GLWallpaperEngine(this);
    }
}
