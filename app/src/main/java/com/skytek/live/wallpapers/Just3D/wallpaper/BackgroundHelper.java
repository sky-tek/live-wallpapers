package com.skytek.live.wallpapers.Just3D.wallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.skytek.live.wallpapers.Just3D.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.skytek.live.wallpapers.Just3D.Constants.BG_FORMAT;
import static com.skytek.live.wallpapers.Just3D.utils.StorageHelper.getBackgroundFolder;

import androidx.constraintlayout.helper.widget.Layer;


class BackgroundHelper {

    private static final String TAG = "BackgroundHelper";

    static List<Layer> loadFromFile(String id, Context context) {
        // Create final list
        List<Layer> output = new ArrayList<>();

        // Get background folder
        File file = getBackgroundFolder(id, context);

        try
        {
            if (file != null) {
                // Directory found
                Log.d(TAG, "Directory " + id + " found in root!");

                File[] layers = file.listFiles();
                if (layers != null) {
                    if (layers.length > 0) {
                        // Sort array by name
                        Arrays.sort(layers);
                        for (File layerFile : layers) {
                            String zString = Utils.getBetweenStrings(layerFile.getPath(), id + "_", BG_FORMAT);
                            Log.d("checkexception" , "exception is "+zString);
                            if(zString != null && !zString.isEmpty()) {
                                int layerZ = Integer.parseInt(zString);
                                Layer layer = new Layer(layerFile, layerZ);
                                output.add(layer);
                                Log.d(TAG, "Layer with name " + layerFile.getName() + " loaded with z=" + layerZ);
                            }
                        }
                    } else {
                        Log.e(TAG, "Directory " + id + " is empty!");
                        return null;
                    }
                }
            } else {
                // Directory not found
                Log.e(TAG, "Directory " + id + " not found in root!");
                return null;
            }
        }
        catch (Exception e)
        {
            Log.d("checkexception " , "exception is for Nexus 5X android M"+e.getMessage());
        }


        return output;
    }

    static Bitmap decodeScaledFromFile(File file) {
        // Get the size
        final BitmapFactory.Options options = new BitmapFactory.Options();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(file.getPath(), options);

                options.inJustDecodeBounds = false;
            }
        });

        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    static Bitmap decodeScaledFromRes(Resources res, int id) {
        // Get the size
        final BitmapFactory.Options options = new BitmapFactory.Options();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeResource(res, id, options);

                options.inJustDecodeBounds = false;
            }
        });



        return BitmapFactory.decodeResource(res, id, options);
    }

    static class Layer {

        private File file;
        private int z;

        Layer(File file, int z) {
            this.file = file;
            this.z = z;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }
}
