package com.skytek.live.wallpapers.Just3D.utils;

import android.net.Uri;

import com.skytek.live.wallpapers.Just3D.data.CatalogItem;
import static com.skytek.live.wallpapers.Just3D.Constants.URL_API;

public class UrlFactory {

    public static String getDownloadUrl(CatalogItem item) {
        Uri.Builder b = Uri.parse(URL_API).buildUpon();
        b.appendPath(item.getId() + ".zip");
        b.appendQueryParameter("raw", "true");
        return b.toString();
    }

    public static String getThumbnailUrl(CatalogItem item) {
        Uri.Builder b = Uri.parse(URL_API).buildUpon();
        b.appendPath(item.getId() + ".png");
        b.appendQueryParameter("raw", "true");
        return b.toString();
    }
    public static String getlikesurl(CatalogItem item) {
        Uri.Builder b = Uri.parse(item.getLikes()).buildUpon();


        return b.toString();
    }
    public static String getdownloadsurl(CatalogItem item) {
        Uri.Builder b = Uri.parse(item.getDownloads()).buildUpon();

        return b.toString();
    }
    public static String getsizeurl(CatalogItem item) {
        Uri.Builder b = Uri.parse(item.getSize()).buildUpon();

        return b.toString();
    }
    public static String getPreviewUrl(CatalogItem item) {
        Uri.Builder b = Uri.parse(URL_API).buildUpon();
        b.appendPath(item.getId() + ".mp4");
        b.appendQueryParameter("raw", "true");
        return b.toString();
    }

    public static String getCatalogUrl() {
        Uri.Builder b = Uri.parse("https://mobipixels.net/3d-Live-wallpapers-api/3d_live_wallpapers.php").buildUpon();
        b.appendQueryParameter("raw", "true");
        return b.toString();
    }
}
