package com.skytek.live.wallpapers.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.skytek.live.wallpapers.Checkvalue;
import com.skytek.live.wallpapers.ModelClasses.datamodel;
import com.skytek.live.wallpapers.R;
import com.skytek.live.wallpapers.TinyData;


import java.util.ArrayList;

public class CustomAdapter<context, text, duration, std> extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    ArrayList<datamodel> localDataset;
    private RadioButton lastCheckedRB = null;
    TinyData tinyData;
    public static String valuefrom;
    public static int languageposition;
    public static  TextView lview;
    static String selectedlang;
    public Checkvalue checkvaue;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ImageView imageView;


        private RadioButton rb;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageView = view.findViewById(R.id.image);
            textView = view.findViewById(R.id.language);
            rb=view.findViewById(R.id.radioB);
            lview=view.findViewById(R.id.selectedlanguage);

        }

        public RadioButton getRb(){

            return  rb;
        }

        public TextView getTextView() {

            return textView;
        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomAdapter(ArrayList<datamodel> dataSet,Checkvalue checkvalue2) {
        localDataset = dataSet;
        checkvaue=checkvalue2;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.languageview, viewGroup, false);
        tinyData=new TinyData(view.getContext());

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataset.get(position).getLanguage());
        viewHolder.rb.setChecked(languageposition==position);
        lastCheckedRB = viewHolder.rb;
        try {
            viewHolder.imageView.setImageResource(localDataset.get(position).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//
//                Toast.makeText(viewHolder.rb.getContext(),viewHolder.textView.getText().toString(), Toast.LENGTH_LONG).show();

                if (lastCheckedRB != null)
                {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = viewHolder.rb;
                localDataset.get(position).setCheck(true);
                languageposition=position;
                viewHolder.rb.setChecked(languageposition==position);
                tinyData.putInt("key",position);
                valuefrom= localDataset.get(position).getLcode();
                selectedlang=localDataset.get(position).getLanguage();
                checkvaue.getcvalue(localDataset.get(position).getLanguage());
            }

        });

        viewHolder.getRb().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (lastCheckedRB != null)
                {
                    lastCheckedRB.setChecked(false);

                }

                lastCheckedRB = viewHolder.rb;
                localDataset.get(position).setCheck(true);
                languageposition=position;
                tinyData.putInt("key",position);
                valuefrom= localDataset.get(position).getLcode();
                selectedlang=localDataset.get(position).getLanguage();
                checkvaue.getcvalue(localDataset.get(position).getLanguage());


            }

        });
    }

    @Override
    public int getItemCount() {

        return localDataset.size();
    }
    public int getItemViewType(int position) {

        return position;
    }

    // Return the size of your dataset (invoked by the layout manager)


}

