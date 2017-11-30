package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_noti;
import com.example.yousangji.howru.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by YouSangJi on 2017-11-02.
 */

public class adt_recy_noti  extends RecyclerView.Adapter<adt_recy_noti.ViewHolder>{
    List<obj_noti> list_noti;
    Context mcontext;
    String userid;

    public adt_recy_noti(Context c, String uid){
        this.mcontext=c;
        this.userid=uid;
        list_noti=new ArrayList<obj_noti>();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view;
        ViewHolder viewHolder=null;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_itm_noti,parent,false);
        viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return list_noti.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtv_noti_message.setText(list_noti.get(position).getNotimessage());
        Glide
                .with(mcontext)
                .load(api_url.API_BASE_URL+"users/profile/"+list_noti.get(position).getProfileurl())
                .bitmapTransform(new CropCircleTransformation(mcontext)).override(30,30)
                .into(holder.img_noti_prof);
    }

    public void putnoti(obj_noti notiobj){
        list_noti.add(notiobj);
        notifyDataSetChanged();
    }

    public void putlist(List<obj_noti> listnoti){
        list_noti=listnoti;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_noti_prof;
        private TextView txtv_noti_message;


        public ViewHolder (View view){
            super(view);
            img_noti_prof=(ImageView)view.findViewById(R.id.img_noti_prof);
            txtv_noti_message=(TextView)view.findViewById(R.id.txt_noti_message);

        }
    }
}
