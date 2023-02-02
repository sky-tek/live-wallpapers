package com.skytek.live.wallpapers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skytek.live.wallpapers.Just3D.MainActivity3d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainLauncher extends AppCompatActivity {

    ImageView offlineob, onlineob, image3dob;

    public static String a;

    AdsManager adsManager;
    public static Boolean adcheck = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlauncher);
        if (Build.VERSION.SDK_INT >= 21)

            getWindow().setStatusBarColor(Color.parseColor("#181725"));
//        MediationTestSuite.launch(MainLauncher.this);
        offlineob = findViewById(R.id.offline);
        onlineob = findViewById(R.id.online);
        image3dob = findViewById(R.id.image3d);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Glide.with(MainLauncher.this).load(R.drawable.live_online)

                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(onlineob);

        Glide.with(MainLauncher.this).load(R.drawable.live_offline)

                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(offlineob);

        Glide.with(MainLauncher.this).load(R.drawable.live3d)
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(image3dob);


        adsManager = new AdsManager();
        adsManager.refreshAd(MainLauncher.this, R.id.fl_adplaceholder1);
        image3dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLauncher.this, MainActivity3d.class);
                startActivity(intent);
                adsManager.show_inter(MainLauncher.this);

                Bundle bundle = new Bundle();
                bundle.putString("live3d","live3d");
                mFirebaseAnalytics.logEvent("live3d",bundle);

            }
        });


        offlineob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                String permission;
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                    permission= Manifest.permission.READ_MEDIA_VIDEO;
                }
                else {
                    permission=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                    //                adsmanager.loadnativeadd(this@facebook_main_activity,r.id.facebook_ad_container)
                    Dexter.withContext(MainLauncher.this)
                            .withPermission(permission).withListener(new PermissionListener() {
                                public void onPermissionDenied(PermissionDeniedResponse param2PermissionDeniedResponse) {
                                    if (param2PermissionDeniedResponse.isPermanentlyDenied())
                                        (new AlertDialog.Builder(MainLauncher.this)).setTitle("Storage Permission").setMessage("Live wallpaper needs to access storage to working properly").setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                                Intent intent = new Intent();
                                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                                intent.setData(Uri.fromParts("package", MainLauncher.this.getApplicationContext().getPackageName(), null));
                                                startActivity(intent);
                                            }
                                        }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                            }
                                        }).show();
                                }

                                public void onPermissionGranted(PermissionGrantedResponse param2PermissionGrantedResponse) {
//                        if (Build.VERSION.SDK_INT >= 23)
                                    Bundle bundle = new Bundle();
                                    bundle.putString("galleryVideo", "galleryVideo");
                                    mFirebaseAnalytics.logEvent("galleryVideo", bundle);
                                    chooseVideo();
                                }

                                public void onPermissionRationaleShouldBeShown(PermissionRequest param2PermissionRequest, PermissionToken param2PermissionToken) {
                                    param2PermissionToken.continuePermissionRequest();
                                }
                            }).check();
                }
        });
        this.onlineob.setOnClickListener(new View.OnClickListener() {


            public void onClick(View param1View) {
                Intent intent = new Intent(MainLauncher.this, MainActivity.class);
                startActivity(intent);
                adsManager.show_inter(MainLauncher.this);
                Bundle bundle = new Bundle();
                bundle.putString("onlineVideo","onlineVideo");
                mFirebaseAnalytics.logEvent("onlineVideo",bundle);

            }
        });

    }


    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(intent, 1);
    }

    public void copyFile(File fromFile, File toFile) {

        Log.d("checkingCopying" , "Going All Good Ninja!");
        //ANR for doing soo much work on main thread
        //fixed using executorservice (shfting task to background to avoid ANR)
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                FileChannel fileChannelInput = null;
                FileChannel fileChannelOutput = null;
                try {
                    fileInputStream = new FileInputStream(fromFile);
                    fileOutputStream = new FileOutputStream(toFile);
                    fileChannelInput = fileInputStream.getChannel();
                    fileChannelOutput = fileOutputStream.getChannel();
                    fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileInputStream != null)
                            fileInputStream.close();
                        if (fileChannelInput != null)
                            fileChannelInput.close();
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                        if (fileChannelOutput != null)
                            fileChannelOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                Cursor cursor = getContentResolver().query(
                        uri, null, null, null, null
                );
                if (cursor == null) {
                    return;
                }
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                a = cursor.getString(nameIndex);
                cursor.close();
                copyFile(new File(getPath(this, uri)), new File(getExternalFilesDir("offline").getAbsolutePath() + "/" + a));


                Intent intent = new Intent(MainLauncher.this, VideoView.class);
//            intent.putExtra("value",name);
                startActivity(intent);

                return;
            } else {
                Toast.makeText(getApplicationContext(), "Select a file first", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MainLauncher.this, exitScreen.class));
        Bundle bundle = new Bundle();
        bundle.putString("exit","exitTotal");
        mFirebaseAnalytics.logEvent("exitTotal",bundle);
        if (adsManager.show_inter(MainLauncher.this)) {
            Bundle bundle1 = new Bundle();
            bundle1.putString("exit","exitShowed");
            mFirebaseAnalytics.logEvent("exitShowed",bundle1);
            adcheck = true;
        } else {
            adcheck = false;
        }
    }

/*    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);


        if (!TextUtils.isEmpty(fileName)) {

            File docsFolder;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                docsFolder = new File(context.getExternalFilesDir(null).getAbsolutePath());
            } else {
                docsFolder = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            }

//            docsFolder = new File(String.valueOf(Environment.getExternalStorageDirectory()));

            if (!docsFolder.exists()) {
                docsFolder.mkdir();
                //    Log.i(TAG, "Created a new directory for PDF");
            }

            File copyFile;

            copyFile = new File(docsFolder + File.separator + fileName);

            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }*/

/*    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
//        if (fileName != null && fileName.startsWith("primary:")) {
//
//            fileName = fileName.replace("primary:","");
//        }
        return fileName;
    }*/


    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        adsManager.load_inter(this);
        super.onResume();
    }

/*    public void data() {
        String mJSONURLString = "http://mobipixels.net/3d-Live-wallpapers-api/front_page.php";

        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(MainLauncher.this);

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                mJSONURLString,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> dataset = new ArrayList<>();

                        try{
                            // Loop through the array elements
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject student = response.getJSONObject(i);

                                // Get the current student (json object) data
                                String imgs = student.getString("imgs");
                                dataset.add(imgs);
                                Log.e("gdsgdsgds", "name"+imgs);
                            }

                            if (dataset.size()==3) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(MainLauncher.this).load(dataset.get(0))

                                                .apply(new RequestOptions()
                                                        .fitCenter()
                                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                                        .override(Target.SIZE_ORIGINAL))
                                                .into(onlineob);

                                        Glide.with(MainLauncher.this).load(dataset.get(1))

                                                .apply(new RequestOptions()
                                                        .fitCenter()
                                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                                        .override(Target.SIZE_ORIGINAL))
                                                .into(offlineob);

                                        Glide.with(MainLauncher.this).load(dataset.get(2))
                                                .apply(new RequestOptions()
                                                        .fitCenter()
                                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                                        .override(Target.SIZE_ORIGINAL))
                                                .into(image3dob);
                                    }
                                });

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){r
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        );
        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);

    }*/


}



