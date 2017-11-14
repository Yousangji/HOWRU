package com.example.yousangji.howru.Controller;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yousangji.howru.Model.obj_filter_thumb;
import com.example.yousangji.howru.R;

import java.util.List;

/**
 * Created by YouSangJi on 2017-11-13.
 */

public class adt_recy_filter extends RecyclerView.Adapter<adt_recy_filter.ThumbnailsViewHolder> {
    private callback_filter filtercallback;
    private List<obj_filter_thumb> list_filter;

    public adt_recy_filter(List<obj_filter_thumb> dataset,callback_filter callback_filter){
        Log.v("mytag", "[adt_recy_filter] Filter Adapter has " + dataset.size() + " items");
        this.list_filter= dataset;
        this.filtercallback = callback_filter;
    }

    @Override
    public ThumbnailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("mytag", "On Create View Holder Called");
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.lay_itm_thumnail, parent, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThumbnailsViewHolder holder, int position) {
        obj_filter_thumb thumb_filter=list_filter.get(position);
        holder.thumbnail.setImageResource(thumb_filter.getImgurl());
        holder.txt_filtername.setText(thumb_filter.getTitle_filter());
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtercallback.onThumbnailClick(thumb_filter.getMagicFilterType());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_filter.size();
    }

    public static class ThumbnailsViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView txt_filtername;


        public ThumbnailsViewHolder(View v) {
            super(v);
            this.txt_filtername=(TextView)v.findViewById(R.id.txt_filter_name);
            this.thumbnail = (ImageView) v.findViewById(R.id.img_filter_thumb);
        }
    }
}
