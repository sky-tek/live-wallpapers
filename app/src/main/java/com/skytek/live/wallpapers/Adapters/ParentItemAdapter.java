package com.skytek.live.wallpapers.Adapters;

//import static com.skytek.live.wallpapers.MainActivity.broadCast;

import static com.skytek.live.wallpapers.Fragments.Home.check_more;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skytek.live.wallpapers.MainActivity;
import com.skytek.live.wallpapers.ModelClasses.ParentItem;
import com.skytek.live.wallpapers.R;
import com.skytek.live.wallpapers.Utilities.Utilities;
import com.skytek.live.wallpapers.categorythumnail;

import java.util.List;

public class ParentItemAdapter extends RecyclerView.Adapter<ParentItemAdapter.ParentViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ParentItem> itemList;
    Context pcontext;

    public ParentItemAdapter(List<ParentItem> itemList, Context context) {
        this.itemList = itemList;
        pcontext = context;
    }
    public ParentItemAdapter() {
    notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        // Here we inflate the corresponding
        // layout of the parent item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item, viewGroup, false);

        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder parentViewHolder, @SuppressLint("RecyclerView") int position) {

        ParentItem parentItem = itemList.get(position);

        parentViewHolder.ParentItemTitle.setText(parentItem.getParentItemTitle());
        parentViewHolder.seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities utilities=new Utilities();
               if (utilities.isOnline(pcontext)) {

                    Intent intent = new Intent(pcontext, categorythumnail.class);
                    intent.putExtra("category", parentItem.getId());
                    pcontext.startActivity(intent);
                } else {
                    Toast.makeText(pcontext, "No Internet connection", Toast.LENGTH_SHORT).show();

                }


            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(parentViewHolder.ChildRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);



        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        layoutManager.setInitialPrefetchItemCount(parentItem.getChildItemList().size());

        // Create an instance of the child
        // item view adapter and set its
        // adapter, layout manager and RecyclerViewPool

        Adapter childItemAdapter = new Adapter(parentItem.getChildItemList(),pcontext,R.layout.child_item);
        parentViewHolder.ChildRecyclerView.setLayoutManager(layoutManager);
        parentViewHolder.ChildRecyclerView.setAdapter(childItemAdapter);
        parentViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool);



    }

    // This method returns the number
    // of items we have added in the
    // ParentItemList i.e. the number
    // of instances we have created
    // of the ParentItemList
    @Override
    public int getItemCount() {

        return itemList.size();
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    class ParentViewHolder
            extends RecyclerView.ViewHolder {

        private TextView ParentItemTitle;
        private RecyclerView ChildRecyclerView;
        private LinearLayout seemore;

        ParentViewHolder(final View itemView) {
            super(itemView);

            seemore = itemView.findViewById(R.id.seemore);
            ParentItemTitle = itemView.findViewById(R.id.parent_item_title);
            ChildRecyclerView = itemView.findViewById(R.id.child_recyclerview);


            if (check_more==true){
                seemore.setVisibility(View.GONE);
            }
            else {
                seemore.setVisibility(View.VISIBLE);
            }
        }

    }

}