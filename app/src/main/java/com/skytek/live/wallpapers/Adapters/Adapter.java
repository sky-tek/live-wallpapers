package com.skytek.live.wallpapers.Adapters;


import static com.skytek.live.wallpapers.MainActivity.context;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skytek.live.wallpapers.GLWallpaperService;
import com.skytek.live.wallpapers.MainActivity;
import com.skytek.live.wallpapers.ModelClasses.ModelClass;
import com.skytek.live.wallpapers.Prefs.PrefConfig;
import com.skytek.live.wallpapers.R;
import com.skytek.live.wallpapers.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<ModelClass> listData;
    public Context mcontext;
    private static DownloadManager manager;
    //    public static String a;
    public static int REQUEST_FOR_ACTIVITY_CODE = 1;
    private ArrayList<String> favlist;
    static public String id1;
    static public String idold;
    static public String idnew;
    int layoutid;
    private ProgressDialog pDialog;


    public Adapter(List<ModelClass> listData, Context context,int id) {
        this.listData = listData;
        mcontext = context;
        layoutid=id;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutid, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ModelClass ld = listData.get(position);

        favlist = PrefConfig.readListFromPref(mcontext);
        if (favlist == null)
            favlist = new ArrayList<>();

        if (favlist.size() != 0) {
            for (int i = 0; i < favlist.size(); i++) {
                if (favlist.get(i).equals(ld.getId())) {
                    holder.favourite.setTag(R.drawable.ic_red_like);
                    holder.favourite.setImageResource(R.drawable.ic_red_like);

                    break;
                } else {
                    holder.favourite.setTag(R.drawable.ic_def_like);
                    holder.favourite.setImageResource(R.drawable.ic_def_like);


                }
            }

        }
        else {
            holder.favourite.setTag(R.drawable.ic_def_like);
            holder.favourite.setImageResource(R.drawable.ic_def_like);
        }

        Glide.with(holder.thumb_path.getContext()).load(ld.getThumb_path())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        holder.progressBar.setVisibility(View.GONE);
//                        File file = new File(mcontext.getExternalFilesDir("live").getAbsolutePath() + "/" + ld.getId());
//                        if (!file.exists()){
//                            holder.download_video.setVisibility(View.VISIBLE);
//                        }
//                        else {
//                            holder.download_video.setVisibility(View.GONE);
//                        }
                        return false;
                    }
                })
                .into(holder.thumb_path);

//        Log.d("dfsdgdsgsdgdsggd", ld.getThumb_path());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permission;
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                    permission=Manifest.permission.READ_MEDIA_VIDEO;
                }
                else {
                    permission=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                Dexter.withContext(mcontext)
                        .withPermission(
                                permission
                        ).withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permission(ld.getId());
//                            holder.downloads.setText(String.valueOf(Integer.parseInt(holder.downloads.getText().toString()) + 1));
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                if (permissionDeniedResponse.isPermanentlyDenied()) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(mcontext)
//set icon
//set title
                                            .setTitle("Storage Permission")
//set message
                                            .setMessage("Live wallpaper needs to access storage to working properly")
//set positive button
                                            .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //set what would happen when positive button is clicked
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", mcontext.getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    mcontext.startActivity(intent);
                                                }
                                            })
//set negative button
                                            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //set what should happen when negative button is clicked
                                                }
                                            }).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        holder.likes.setText(ld.getLikes());
        holder.downloads.setText(ld.getDownloads());

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favourite.getTag().equals(R.drawable.ic_def_like)) {
                    Glide.with(holder.favourite.getContext())
                            .load(R.drawable.like_process)
                            .into(holder.favourite);

                    String like = "https://mobipixels.net/3d-Live-wallpapers-api/likes_live_wp.php?id=" + ld.getId() + "&likes=1";


                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(context);

                    // making a string request to update our data and
                    // passing method as PUT. to update our data.
                    StringRequest request = new StringRequest(Request.Method.PUT, like, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            holder.likes.setText(String.valueOf(Integer.parseInt(holder.likes.getText().toString()) + 1));
                            holder.favourite.setTag(R.drawable.ic_red_like);
                            holder.favourite.setImageResource(R.drawable.ic_red_like);

                            favlist.add(ld.getId());
                            PrefConfig.writeListInPref(mcontext, favlist);

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // displaying toast message on response failure.
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(request);


                }
                else if (holder.favourite.getTag().equals(R.drawable.ic_red_like)){

                    String like = "https://mobipixels.net/3d-Live-wallpapers-api/likes_live_wp.php?id=" + ld.getId() + "&likes=0";


                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(context);

                    // making a string request to update our data and
                    // passing method as PUT. to update our data.
                    StringRequest request = new StringRequest(Request.Method.PUT, like, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            holder.likes.setText(String.valueOf(Integer.parseInt(holder.likes.getText().toString()) - 1));
                            holder.favourite.setTag(R.drawable.ic_def_like);
                            holder.favourite.setImageResource(R.drawable.ic_def_like);


//                            listData.get(position);
                            favlist.remove(ld.getId());
                            PrefConfig.writeListInPref(mcontext, favlist);

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // displaying toast message on response failure.
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(request);


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumb_path;
//        private ImageView download_video;
        private TextView likes;
        private TextView downloads;
        private ImageView favourite;
        private ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);

            thumb_path = itemView.findViewById(R.id.catalog_preview);
            likes = itemView.findViewById(R.id.likes);
            downloads = itemView.findViewById(R.id.downloads);
            favourite = itemView.findViewById(R.id.fav_like);
            progressBar = itemView.findViewById(R.id.catalog_progress);
//            download_video = itemView.findViewById(R.id.download_video);
            Log.d("checkpermission" , "asdasdas is ");

        }

    }


    @SuppressLint("MissingPermission")
    public void permission(String id) {
        id1 = id;

        Log.d("checkpermission" , "permission is ");
            File file = new File(mcontext.getExternalFilesDir("live").getAbsolutePath() + "/" + id);
            if (file.exists()) {
                try
                {
                    final Intent intent1 = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent1.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, GLWallpaperService.class));
                    ((Activity) mcontext).startActivityForResult(intent1, REQUEST_FOR_ACTIVITY_CODE);

                }
                catch (Exception e)
                {
                    //IGNORE
                }



            } else {
                Utilities utilities=new Utilities();
                if (utilities.isOnline(mcontext)) {
//                    Base_ThemeOverlay_MaterialComponents_Dialog
//                    Base_Theme_MaterialComponents_Dialog_Alert
//                    Widget_MaterialComponents_Snackbar
                pDialog = new ProgressDialog(mcontext,R.style. Base_ThemeOverlay_MaterialComponents_Dialog);
                pDialog.setTitle("      "+mcontext.getString(R.string.main_ongoing_download));
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setProgressNumberFormat("");
                pDialog.setProgressPercentFormat(null);
                pDialog.setCancelable(false);
                pDialog.show();


                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());


                executor.execute(() ->

                {

                    String image = "https://mobipixels.net/3d-Live-wallpapers-api/Live_wall_single_wall.php?id=" + id;


                    RequestQueue requestQueue = Volley.newRequestQueue(mcontext);

                    // Initialize a new JsonArrayRequest instance
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                            Request.Method.GET,
                            image,
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    // Do something with response
                                    //mTextView.setText(response.toString());

                                    try {
                                        String image_path = null;
                                        for (int i = 0; i < response.length(); i++) {

                                            JSONObject student = response.getJSONObject(i);

                                            // Get the current student (json object) data
                                            image_path = student.getString("img_path");

                                        }
                                        download(image_path);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pDialog.dismiss();
                                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                    );
                    requestQueue.add(jsonArrayRequest);
                    handler.post(() -> {


                    });
                });


            }
                else {
                    Toast.makeText(mcontext, "No Internet", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void download(String image_path) {

        String download = "https://mobipixels.net/3d-Live-wallpapers-api/downloads_live_wp.php?id=" + id1 + "&down=1";


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // making a string request to update our data and
        // passing method as PUT. to update our data.
        StringRequest request = new StringRequest(Request.Method.PUT, download, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                new DownloadFileFromURL().execute(image_path);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // displaying toast message on response failure.
                pDialog.dismiss();
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

//    @SuppressLint("MissingPermission")
//    public static void downloadManager(String url, Context context) {
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setDescription("download");
//// in order for this if to run, you must use the android 3.2 to compile your app
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
////        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        request.setDestinationInExternalFilesDir(context, "live", "" + "/" + id1);
//// get download service and enqueue file
//        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        manager.enqueue(request);
//
//
//    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private Boolean RESULT_FAIL = false;

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String storageDir = mcontext.getExternalFilesDir("live").getAbsolutePath() + "/";

                File imageFile = new File(storageDir + id1);
                OutputStream output = new FileOutputStream(imageFile);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    output.write(data, 0, count);
                }
                output.flush();

                output.close();
                input.close();
                RESULT_FAIL = false;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                RESULT_FAIL = true;
            }

            return null;

        }

        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {

                if (RESULT_FAIL) {

                    File file = new File(mcontext.getExternalFilesDir("live").getAbsolutePath() + "/" + id1);
                    if (file.exists()) {
                        file.delete();
                    }

                    Toast.makeText(mcontext, "Internet Connection Failed", Toast.LENGTH_SHORT).show();
                } else {

                    try
                    {
                        final Intent intent1 = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                        intent1.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(context, GLWallpaperService.class));
                        ((Activity) context).startActivityForResult(intent1, REQUEST_FOR_ACTIVITY_CODE);
                    }
                    catch (Exception e)
                    {

                    }

                 notifyDataSetChanged();

                }
                pDialog.dismiss();


        }


    }



}