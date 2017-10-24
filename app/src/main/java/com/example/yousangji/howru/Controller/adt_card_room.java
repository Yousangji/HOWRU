package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.View.viewer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class adt_card_room extends RecyclerView.Adapter<adt_card_room.cardviewholder>{

    private Context mContext;
    private List<obj_room> roomObjList;

    public class cardviewholder extends RecyclerView.ViewHolder {
        public TextView title, count, nickname;
        public ImageView thumbnail, onair;

        public cardviewholder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            nickname = (TextView) view.findViewById(R.id.card_nickname);
            count = (TextView) view.findViewById(R.id.card_count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            onair=(ImageView)view.findViewById(R.id.onair);

        }
    }

    public  adt_card_room(Context mContext, List<obj_room> roomlist) {
        this.mContext = mContext;
        this.roomObjList = roomlist;
    }

    @Override
    public cardviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_itm_mainhome,parent,false);
        return new cardviewholder(itemview);
    }

    @Override
    public void onBindViewHolder(cardviewholder holder, int position) {
        final obj_room rmobj=roomObjList.get(position);
        holder.title.setText(rmobj.getTitle());
        holder.nickname.setText(rmobj.getNickname());
        holder.count.setText(String.valueOf(rmobj.getCount()));
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson=new GsonBuilder().create();
                String rmjsonstr=gson.toJson(rmobj);
                Intent fmthm_torm= new Intent(mContext,viewer.class);
                fmthm_torm.putExtra("rminfo",rmjsonstr);
                mContext.startActivity(fmthm_torm);
            }
        });

        Glide
                .with(mContext)
                .load(api_url.API_BASE_URL+rmobj.getThumnailurl())
                .placeholder(R.drawable.back)
                .error(R.drawable.back)
                .into(holder.thumbnail);

        if(rmobj.getOnair()==0){
            holder.onair.setVisibility(View.VISIBLE);
        }else{
            holder.onair.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return roomObjList.size();
    }

    public void addroomlist(List<obj_room> add){
        roomObjList.addAll(add);
        notifyDataSetChanged();
    }

    public void setlist(List<obj_room> set){
        roomObjList=set;
        notifyDataSetChanged();
    }
}
