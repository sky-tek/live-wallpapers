package com.skytek.live.wallpapers.Just3D.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.skytek.live.wallpapers.Just3D.data.CatalogItem;
import com.skytek.live.wallpapers.Just3D.utils.UrlFactory;
import com.skytek.live.wallpapers.Prefs.PrefConfig3d;
import com.skytek.live.wallpapers.R;
import java.util.ArrayList;
import java.util.List;

public class CatalogAdapter extends BaseAdapter {

    // Data
    private final List<CatalogItem> itemList;

    private final Context context;
    private final LayoutInflater inflater;
    private ArrayList<String> favlist;
    public CatalogAdapter(Context context) {
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        itemList = new ArrayList<>();


    }

    public void setContent(List<CatalogItem> itemList) {
        this.itemList.clear();

        this.itemList.addAll(itemList);

        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null) {
            vi = inflater.inflate(R.layout.catalog_cell, parent, false);
        }

        CatalogItem item = itemList.get(position);

        // Set title
//        TextView title = vi.findViewById(R.id.catalog_text_title);
//        title.setText(item.getTitle());

        // Set author
//        TextView author = vi.findViewById(R.id.catalog_text_author);
//        author.setText(item.getAuthor());

        // Set offline
//        ImageView offline = vi.findViewById(R.id.catalog_img_offline);

//        if (backgroundExist(item.getId(), context)) {
//            offline.setVisibility(View.VISIBLE);
//        } else {
//            offline.setVisibility(View.INVISIBLE);
//        }

        // Set thumbnail
        ImageView preview = vi.findViewById(R.id.catalog_preview);
        TextView likes = vi.findViewById(R.id.likes);
        TextView downloads = vi.findViewById(R.id.downloads);
        TextView size = vi.findViewById(R.id.size);
        ImageView fav_like = vi.findViewById(R.id.fav_like);

        final ProgressBar progressBar = vi.findViewById(R.id.catalog_progress);
        String thumbnailUrl = UrlFactory.getThumbnailUrl(item);
        String likesurl = UrlFactory.getlikesurl(item);
        String downloadsurl = UrlFactory.getdownloadsurl(item);
        String sizeurl = UrlFactory.getsizeurl(item);

        likes.setText(likesurl);
        downloads.setText(downloadsurl);
        size.setText(sizeurl);
        favlist = PrefConfig3d.readListFromPref3d(context);
        if (favlist == null)
            favlist = new ArrayList<>();
        if (favlist.size() != 0) {
            for (int i = 0; i < favlist.size(); i++) {
                if (favlist.get(i).equals(item.getId())) {
                    fav_like.setTag(R.drawable.ic_red_like);
                    fav_like.setImageResource(R.drawable.ic_red_like);
                    break;
                } else {
                   fav_like.setTag(R.drawable.ic_def_like);
                    fav_like.setImageResource(R.drawable.ic_def_like);

                }
            }
        }
        else {
            fav_like.setTag(R.drawable.ic_def_like);
            fav_like.setImageResource(R.drawable.ic_def_like);
        }

        Glide.with(context).load(thumbnailUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                       progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(preview);

//        Glide.with(context)
//                .load(thumbnailUrl)
//                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .centerCrop()
//                .placeholder(R.drawable.empty)
//                .crossFade()
//                .listener(new RequestListe() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(preview);

        fav_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fav_like.getTag().equals(R.drawable.ic_def_like))
                {
                    Glide.with(fav_like.getContext())
                            .load(R.drawable.like_process)
                            .into(fav_like);

                    String like = "https://mobipixels.net/3d-Live-wallpapers-api/likes_3d_wp.php?id=" + item.getId() + "&likes=1";


                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(context);

                    // making a string request to update our data and
                    // passing method as PUT. to update our data.
                    StringRequest request = new StringRequest(Request.Method.PUT, like, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString()) + 1));
                            fav_like.setTag(R.drawable.ic_red_like);
                            fav_like.setImageResource(R.drawable.ic_red_like);
                            favlist.add(item.getId());
                            PrefConfig3d.writeListInPref3d(context, favlist);
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
                else if (fav_like.getTag().equals(R.drawable.ic_red_like))
                    {
                    String like = "https://mobipixels.net/3d-Live-wallpapers-api/likes_3d_wp.php?id=" + item.getId() + "&likes=0";


                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(context);

                    // making a string request to update our data and
                    // passing method as PUT. to update our data.
                    StringRequest request = new StringRequest(Request.Method.PUT, like, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            likes.setText(String.valueOf(Integer.parseInt(likes.getText().toString()) -1));
                            fav_like.setTag(R.drawable.ic_def_like);
                            fav_like.setImageResource(R.drawable.ic_def_like);
                            favlist.remove(item.getId());
                            PrefConfig3d.writeListInPref3d(context, favlist);
//                            notifyDataSetChanged();
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



        return vi;
    }
}
