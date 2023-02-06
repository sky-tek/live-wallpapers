package com.skytek.live.wallpapers;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skytek.live.wallpapers.Adapters.CustomAdapter;
import com.skytek.live.wallpapers.Just3D.MainActivity3d;
import com.skytek.live.wallpapers.ModelClasses.datamodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainLauncher extends AppCompatActivity implements Checkvalue {

    ImageView offlineob, onlineob, image3dob;

    public static String a;

    ImageView checkLangauge;
    AdsManager adsManager;
    Checkvalue checkvalue;
    ArrayList<datamodel> listoflanguage = new ArrayList<>();
    public static Boolean adcheck = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    public String one;
    public String getcode;

    public static TextView selectedl;

    AppUpdateManager appUpdateManager;



    private static final int IN_APP_UPDATE_MY_REQUEST_CODE = 123;

    SharedPreferences prefs;

    @SuppressLint({"MissingInflatedId", "SuspiciousIndentation"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainlauncher);

        adsManager = new AdsManager();
        adsManager.load_inter(this);



        listoflanguage.add(new datamodel(R.drawable.afrika,"Afrikaans", false, "af"));
        listoflanguage.add(new datamodel(R.drawable.albania,"Albanian", false, "sq"));
        listoflanguage.add(new datamodel(R.drawable.ethopia,"Amharic", false, "am"));
        listoflanguage.add(new datamodel(R.drawable.saudi_arabia,"Arabic", false, "ar"));
        listoflanguage.add(new datamodel(R.drawable.armenia,"Armenian", false, "hy"));
        listoflanguage.add(new datamodel(R.drawable.azerbaijan,"Azerbaijani", false, "az"));
        listoflanguage.add(new datamodel(R.drawable.basque_country,"Basque", false, "eu"));
        listoflanguage.add(new datamodel(R.drawable.belarus_flag,"Belarusian", false, "be"));
        listoflanguage.add(new datamodel(R.drawable.bulgaria,"Bulgarian", false, "bg"));
        listoflanguage.add(new datamodel(R.drawable.maynmar,"Burmese", false, "my"));
        listoflanguage.add(new datamodel(R.drawable.catalan,"Catalan", false, "ca"));
        listoflanguage.add(new datamodel(R.drawable.china,"Chinese", false, "zh"));
        listoflanguage.add(new datamodel(R.drawable.crotia,"Croatian", false, "hr"));
        listoflanguage.add(new datamodel(R.drawable.czech,"Czech", false, "cs"));
        listoflanguage.add(new datamodel(R.drawable.denmark,"Danish", false, "da"));
        listoflanguage.add(new datamodel(R.drawable.dutch_flag,"Dutch", false, "nl"));
        listoflanguage.add(new datamodel(R.drawable.englandflag,"English", false, "en"));
        listoflanguage.add(new datamodel(R.drawable.estonia_flag,"Estonian", false, "et"));
        listoflanguage.add(new datamodel(R.drawable.filipino_flag,"Filipino", false, "et"));
        listoflanguage.add(new datamodel(R.drawable.finland_flag,"Finnish", false, "fi"));
        listoflanguage.add(new datamodel(R.drawable.france_flag,"French", false, "fr"));
        listoflanguage.add(new datamodel(R.drawable.spanish_flag,"Galician", false, "gl"));
        listoflanguage.add(new datamodel(R.drawable.georgia_flag,"Georgian", false, "ka"));
        listoflanguage.add(new datamodel(R.drawable.germany_flag,"German", false, "de"));
        listoflanguage.add(new datamodel(R.drawable.greek_flag,"Greek", false, "el"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Gujarati", false, "gu"));
        listoflanguage.add(new datamodel(R.drawable.hebrew_israel_flag,"Hebrew", false, "iw"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Hindi", false, "hi"));
        listoflanguage.add(new datamodel(R.drawable.hungary_flag,"Hungarian", false, "hu"));
        listoflanguage.add(new datamodel(R.drawable.iceland,"Icelandic", false, "is"));
        listoflanguage.add(new datamodel(R.drawable.indonesia_flag,"Indonesian", false, "id"));
        listoflanguage.add(new datamodel(R.drawable.italy,"Italian", false, "it"));
        listoflanguage.add(new datamodel(R.drawable.japan_flag,"Japanese", false, "ja"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Kannada", false, "kn"));
        listoflanguage.add(new datamodel(R.drawable.kazakh,"Kazakh", false, "kk"));
        listoflanguage.add(new datamodel(R.drawable.cambodia,"Khmer", false, "km"));
        listoflanguage.add(new datamodel(R.drawable.korea_flag,"Korean", false, "ko"));
        listoflanguage.add(new datamodel(R.drawable.kyrgyzstan,"Kyrgyz", false, "ky"));
        listoflanguage.add(new datamodel(R.drawable.lao,"Lao", false, "lo"));
        listoflanguage.add(new datamodel(R.drawable.latvia_flag,"Latvian", false, "lv"));
        listoflanguage.add(new datamodel(R.drawable.lithuania_flag,"Lithuanian", false, "lt"));
        listoflanguage.add(new datamodel(R.drawable.macedonia_flag,"Macedonian", false, "mk"));
        listoflanguage.add(new datamodel(R.drawable.malay_flag,"Malay", false, "ms"));
        listoflanguage.add(new datamodel(R.drawable.malay_flag,"Malay (Malaysia)", false, "ms"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Malayalam", false, "ml"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Marathi", false, "mr"));
        listoflanguage.add(new datamodel(R.drawable.mongolia,"Mongolian", false, "mn"));
        listoflanguage.add(new datamodel(R.drawable.nepal,"Nepali", false, "ne"));
        listoflanguage.add(new datamodel(R.drawable.norway_flag,"Norwegian", false, "no"));
//        listoflanguage.add(new datamodel(R.drawable.iran_flag,"Persian", false, "fa"));
        listoflanguage.add(new datamodel(R.drawable.poland_flag,"Polish", false, "pl"));
        listoflanguage.add(new datamodel(R.drawable.brazil,"Portuguese (Brazil)", false, "pt"));
        listoflanguage.add(new datamodel(R.drawable.portugal_flag,"Portuguese (Portugal)", false, "pt"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Punjabi", false, "pa"));
        listoflanguage.add(new datamodel(R.drawable.romanian_flag,"Romanian", false, "ro"));
        listoflanguage.add(new datamodel(R.drawable.russia,"Russian", false, "ru"));
        listoflanguage.add(new datamodel(R.drawable.serbia,"Serbian", false, "sr"));
        listoflanguage.add(new datamodel(R.drawable.srilanka,"Sinhala", false, "si"));
        listoflanguage.add(new datamodel(R.drawable.slovak_flag,"Slovak", false, "sk"));
        listoflanguage.add(new datamodel(R.drawable.slovenia_flag,"Slovenian", false, "sl"));
        listoflanguage.add(new datamodel(R.drawable.spanish_flag,"Spanish (American)", false, "es"));
        listoflanguage.add(new datamodel(R.drawable.spanish_flag,"Spanish (Spain)", false, "es"));
        listoflanguage.add(new datamodel(R.drawable.spanish_flag,"Spanish (US)", false, "es"));
        listoflanguage.add(new datamodel(R.drawable.kenya_flag_swahili_language,"Swahili", false, "sw"));
        listoflanguage.add(new datamodel(R.drawable.sweden_flag,"Swedish", false, "sv"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Tamil", false, "ta"));
        listoflanguage.add(new datamodel(R.drawable.india_flag,"Telugu", false, "te"));
        listoflanguage.add(new datamodel(R.drawable.thailand_flag,"Thai", false, "th"));
        listoflanguage.add(new datamodel(R.drawable.turkey_flag,"Turkish", false, "tr"));
        listoflanguage.add(new datamodel(R.drawable.ukraine_flag,"Ukrainian", false, "uk"));
        listoflanguage.add(new datamodel(R.drawable.pakistan,"Urdu", false, "ur"));
        listoflanguage.add(new datamodel(R.drawable.vietnamese_flag,"Vietnamese", false, "vi"));
        listoflanguage.add(new datamodel(R.drawable.afrika,"Zulu", false, "zu"));

        initInAppUpdate();
        if (Build.VERSION.SDK_INT >= 21)

           checkLangauge =  findViewById(R.id.checkLangauge);
            getWindow().setStatusBarColor(Color.parseColor("#181725"));
//        MediationTestSuite.launch(MainLauncher.this);
        offlineob = findViewById(R.id.offline);
        onlineob = findViewById(R.id.online);
        image3dob = findViewById(R.id.image3d);

        checkvalue = this;

        checkLangauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diloglanguage();
            }
        });

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


    //app update manager
    public void initInAppUpdate() {
        Log.d("checkappupdate" , "checking the calling functio  here");
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // This example applies an immediate update. To apply a flexible update
                        // instead, pass in AppUpdateType.FLEXIBLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        Log.d("checkappupdate" , "checking the calling functio try  here");
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, MainLauncher.this, IN_APP_UPDATE_MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d("checkappupdate" , "checking the calling functio catch  here");
                        e.printStackTrace();
                    }
                }
                else
                {
                    Log.d("checkappupdate" , "asdsad checking the calling functio try  here");
                }
            }

        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }
    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdate();
            }
        }
    };
    private void showCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New update is ready!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Install", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.show();
    }

    @Override
    protected void onStop() {
        if (appUpdateManager != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
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
        if (requestCode == IN_APP_UPDATE_MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "onActivityResult: Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
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

    public void diloglanguage() {
        Log.d("adasdasd" ,"asdadasdasasdsadasd123123123dasd");
        final Dialog dialog = new Dialog(MainLauncher.this);
        dialog.setContentView(R.layout.activity_dilogactivity);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.custom1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainLauncher.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(listoflanguage, checkvalue);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
        Button dialogButtonselect = (Button) dialog.findViewById(R.id.select);
        Button dialogButtoncancel = (Button) dialog.findViewById(R.id.dismiss);
        Log.d("adasdasd" ,"asdaliasd");

        selectedl = dialog.findViewById(R.id.selectedlanguage);

        dialogButtonselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogButtonselect.setBackgroundColor(getResources().getColor(R.color.black));
                one = CustomAdapter.valuefrom;
                prefs = getSharedPreferences("Lcodeget", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Lcodeget", one);
                editor.apply();
                getcode = prefs.getString("Lcodeget", "en");
                Locale locale4 = new Locale(getcode);
                Configuration config4 = getBaseContext().getResources().getConfiguration();
                config4.locale = locale4;
                getBaseContext().getResources().updateConfiguration(config4, getBaseContext().getResources().getDisplayMetrics());
                Intent i = new Intent(getApplicationContext(), MainLauncher.class);
                startActivity(i);
                dialog.dismiss();
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                myEdit.putString("language", selectedl.getText().toString());


                myEdit.commit();

                Toast.makeText(getApplicationContext(), "Language is Changed!!", LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }
    @Override
    public String getcvalue(String value) {
        selectedl.setText(value);
        return value;
    }
}



