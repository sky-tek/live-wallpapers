package com.skytek.live.wallpapers.Just3D;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.falcon.video.downloader.Helper.AdsManager;
import com.google.android.material.snackbar.Snackbar;
import com.skytek.live.wallpapers.Just3D.async.MyAsync;
import com.skytek.live.wallpapers.Just3D.async.WallpaperDownloader;
import com.skytek.live.wallpapers.Just3D.components.CatalogAdapter;
import com.skytek.live.wallpapers.Just3D.components.InfoView;
import com.skytek.live.wallpapers.Just3D.components.MySwipeRefreshLayout;
import com.skytek.live.wallpapers.Just3D.data.Catalog;
import com.skytek.live.wallpapers.Just3D.data.CatalogItem;
import com.skytek.live.wallpapers.Just3D.services.SyncManager;
import com.skytek.live.wallpapers.Just3D.utils.InternalData;
import com.skytek.live.wallpapers.Just3D.utils.StorageHelper;
import com.skytek.live.wallpapers.R;

import java.io.File;

import static com.skytek.live.wallpapers.Just3D.Constants.LD_TIMESTAMP;
import static com.skytek.live.wallpapers.Just3D.Constants.PREF_CHECKSENS;
import static com.skytek.live.wallpapers.Just3D.Constants.PREF_CHECKSENS_DEFAULT;
import static com.skytek.live.wallpapers.Just3D.Constants.T_CATALOG_EXPIRATION;
import static com.skytek.live.wallpapers.Just3D.Utils.getTimestamp;
import static com.skytek.live.wallpapers.Just3D.Utils.openLWSetter;
import static com.skytek.live.wallpapers.Just3D.utils.StorageHelper.backgroundExist;
import static com.skytek.live.wallpapers.Just3D.utils.StorageHelper.getCacheFolder;

public class MainActivity3d extends AppCompatActivity implements MyAsync.MyAsyncInterface, SwipeRefreshLayout.OnRefreshListener {

    // Log
    private final String TAG = getClass().getSimpleName();


    // Constants
    public static final String EXTRA_DISABLE_NOTIFICATION = "disable_notification";

    // Layout
    private View rootView;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private InfoView infoView;

    // Catalog
    private CatalogAdapter catalogAdapter;
    private Catalog catalog;
//    private DatabaseReference mDatabase;

    //ModelClas Recycler
//    ArrayList<ModelClass> listhabhai=new ArrayList<>();
//    private RecyclerView recycleHa;
//
//    Adapter ad;


    // Broadcast listener
    private final IntentFilter broadcastFilter = new IntentFilter(SyncManager.ACTION_SYNC);
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int result = intent.getIntExtra(SyncManager.EXTRA_RESULT, SyncManager.RESULT_FAIL);
                swipeRefreshLayout.setRefreshing(false); // Stop refresh layout
                if (result == SyncManager.RESULT_SUCCESS) {
                    // Reload local content
                    if (loadLocalContent()) {
                        refreshList();
                    } else {
                        // This error is very unlikely to happen
                        showCatalogDownloadError();
                    }
                } else if (result == SyncManager.RESULT_TIMEOUT) {
                    // Connection timeout
                    showTimeoutError();
                } else {
                    // Catalog sync failed
                    showCatalogDownloadError(); // Show catalog download error
                }
            }
        }
    };

    private Context context;

    SharedPreferences sharedPreferences5;
    private AdsManager adsManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3d);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#181725"));
        }


             adsManager = new AdsManager();
            adsManager.loadNativeAdd(MainActivity3d.this,R.id.fl_adplaceholder1);


            context = MainActivity3d.this;


            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            // Layout
            rootView = findViewById(R.id.main_container);
            GridView catalogList = findViewById(R.id.catalog_grid);
            swipeRefreshLayout = (MySwipeRefreshLayout) rootView;
            infoView = findViewById(R.id.info_view);

            // Swipe down refresh
            swipeRefreshLayout.setOnRefreshListener(this);
            //recycleHa=findViewById(R.id.recyclerwallpaper);


            swipeRefreshLayout.setSwipeableChildren(R.id.catalog_grid);

            catalogAdapter = new CatalogAdapter(this);
            catalog = new Catalog();
            catalogList.setAdapter(catalogAdapter);
            catalogList.setOnItemClickListener(catalogItemClickListener);

            boolean checkSensors = sharedPreferences.getBoolean(PREF_CHECKSENS, PREF_CHECKSENS_DEFAULT);
            if (!Utils.sensorsAvailable(this) && checkSensors) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.main_dialog_nosensor_title)
                        .setMessage(R.string.main_dialog_nosensor_message)
                        .setNegativeButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferences.edit().putBoolean(PREF_CHECKSENS, false).apply();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            infoView.show(R.string.main_info_empty_title, R.string.main_info_empty_message);
        }


    @Override
    protected void onResume() {
        super.onResume();

        loadRemoteContent();
        // Register broadcast listener
        registerReceiver(broadcastReceiver, broadcastFilter);

        // Check if refresh is running without service
        if (swipeRefreshLayout.isRefreshing() && !SyncManager.isRunning()) {
            swipeRefreshLayout.setRefreshing(false);
        }

        // Load catalog if available
        loadLocalContent();
        refreshList();

        // Check if catalog is expired
        InternalData internalData = new InternalData(this);
        long lastTimestamp = internalData.readLong(LD_TIMESTAMP, 0);
        long delta = getTimestamp() - lastTimestamp;

        if (delta < 0 || delta > T_CATALOG_EXPIRATION || catalog.size() == 0) {
            // Catalog has expired download
            Log.d(TAG, "Catalog has reached expiration");
            loadRemoteContent();
        } else {
            Log.d(TAG, "Catalog is still valid!");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    //     Download Listener
    @Override
    public void onCompleted(int id, Bundle extra) {
        if (id == WallpaperDownloader.ID) {
            CatalogItem downloadedItem = extra.getParcelable(WallpaperDownloader.EXTRA_CATALOG_ITEM);

            startSetActivity(downloadedItem);
            refreshList(); // refresh list for downloaded icon
        }
    }

    @Override
    public void onFailed(int id, Bundle bundle) {
        if (id == WallpaperDownloader.ID) {
            swipeRefreshLayout.setRefreshing(false); // Stop refresh layout
            int errorCode = bundle.getInt(WallpaperDownloader.EXTRA_FAIL_CODE);
            if (errorCode == WallpaperDownloader.FAIL_CODE_TIMEOUT) {
                showTimeoutError();
            } else {
                showBackgroundDownloadError(); // Show background download error
            }
        }
    }

    //     Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//
//        menu.findItem(R.id.main_menu_sort).getSubMenu().getItem(0).setChecked(true);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.main_menu_refresh:
//                // Reload catalog from the server
//                loadRemoteContent();
//                return true;
//
//            case R.id.main_menu_set:
//                // Show live-wallpaper preview
//                openLWSetter(context);
//                return true;
//
////            case R.id.main_menu_settings:
////                // Start settings
////                Intent intent = new Intent(context, SettingsActivity.class);
////                startActivity(intent);
////                return true;
//
//            // Sort
//            case R.id.main_menu_sort_new:
//                // New
//                item.setChecked(true);
//                catalog.sort(Catalog.SORT_BY_NEW);
//                refreshList();
//                return true;
////
//            case R.id.main_menu_sort_title:
//                // Name
//                item.setChecked(true);
//                catalog.sort(Catalog.SORT_BY_TITLE);
//                refreshList();
//                return true;
////
//            case R.id.main_menu_sort_author:
//                // Author
//                item.setChecked(true);
//                catalog.sort(Catalog.SORT_BY_AUTHOR);
//                refreshList();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    // Catalog click listeners
    private final AdapterView.OnItemClickListener catalogItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Check if background is available offline
            final CatalogItem catalogItem = catalog.get(position);
//            if (backgroundExist(catalogItem.getId(), context)) {
//                // Background already downloaded
//                startSetActivity(catalogItem);
//            } else {
                downloadBackground(catalogItem);
                //setShowPreview(catalogItem);
//            }
        }
    };

    @Override
    public void onRefresh() {
        loadRemoteContent();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PreviewActivity.PREVIEW_ACTIVITY_REQUEST_CODE && resultCode == PreviewActivity.RESULT_OK) {
            if (data != null) {
                CatalogItem catalogItem = data.getParcelableExtra(PreviewActivity.EXTRA_CATALOG_ITEM);
                if (catalogItem != null) {
                    downloadBackground(catalogItem);
                }
            }
        }
    }

    // Refresh list and display errors
    private void refreshList() {
        // Check if there's content
        if (catalog != null && catalog.size() == 0) {
            // Display error message
            Log.d(TAG, "Refresh called but catalog is empty!");
            return;
        }

        // Infoview is no longer needed
        infoView.hide();

        // Set list content to list
        catalogAdapter.setContent(catalog);

    }

    private boolean loadLocalContent() {
        return catalog.loadFromCache(context);
    }

    // Load catalog remotely
    private void loadRemoteContent() {

        // Clear cache
        StorageHelper.deleteFolder(getCacheFolder(this));

        if (Utils.checkConnection(this)) {
            // Start the refresh layout
            swipeRefreshLayout.setRefreshing(true);

            // Start the download
            SyncManager.start(context, false);
        } else {
            // Stop refresh layout
            swipeRefreshLayout.setRefreshing(false);

            // Show connection error
            Snackbar.make(rootView, R.string.error_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(WallpaperDownloader.progressDialog != null)
        {
            WallpaperDownloader.progressDialog.dismiss();
        }

    }

    // Download background
    private void downloadBackground(final CatalogItem item) {
        // Download
        new WallpaperDownloader(context, item)
                .setListener((MyAsync.MyAsyncInterface) context)
                .execute();
    }

    // Set background id as wallpaper
    private void startSetActivity(CatalogItem catalogItem) {
        // Start set activity
        Intent intent = new Intent(context, SetActivity.class);
        intent.putExtra(SetActivity.EXTRA_CATALOG_ITEM, catalogItem);
        startActivity(intent);
        String download="https://mobipixels.net/3d-Live-wallpapers-api/downloads_3d_wp.php?id="+ catalogItem.getId()+"&down=1";
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // making a string request to update our data and
        // passing method as PUT. to update our data.
        StringRequest request = new StringRequest(Request.Method.PUT, download, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(MainActivity3d.this, "Download completed", Toast.LENGTH_SHORT).show();
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

    // Start preview activity
    private void setShowPreview(CatalogItem catalogItem) {
        // Start preview activity
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_CATALOG_ITEM, catalogItem);
        startActivityForResult(intent, PreviewActivity.PREVIEW_ACTIVITY_REQUEST_CODE);
    }

    // Alerts
    private void showCatalogDownloadError() {
        Snackbar
                .make(rootView, R.string.error_catalog, Snackbar.LENGTH_LONG)
                .setAction(R.string.common_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadRemoteContent();
                    }
                })
                .show();
    }

    private void showBackgroundDownloadError() {
        Snackbar.make(rootView, R.string.error_download, Snackbar.LENGTH_LONG).show();
    }

    private void showTimeoutError() {
        Snackbar.make(rootView, R.string.error_timeout, Snackbar.LENGTH_LONG).show();
    }
}

