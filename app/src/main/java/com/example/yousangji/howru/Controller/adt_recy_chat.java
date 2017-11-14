package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_chatmsg;
import com.example.yousangji.howru.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class adt_recy_chat  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<obj_chatmsg> list_chatmsg;
    private Context context;
    private obj_chatmsg nwmsgobj=null;

    public  adt_recy_chat(Context contextini, List<obj_chatmsg> list_chatmsgini){
        this.list_chatmsg=list_chatmsgini;
        this.context=contextini;
    }


    public adt_recy_chat(Context contextini){
        this.context=contextini;
        this.list_chatmsg=new ArrayList<obj_chatmsg>();
        //list_chatmsg.add(new obj_chatmsg());
        //list_chatmsg.add(new obj_chatmsg());
    }


    @Override
    public int getItemCount() {
        return this.list_chatmsg.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder=null;
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_itm_chat,parent,false);
        viewHolder=new VH_chatmsg(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((VH_chatmsg)holder).msg_nickname.setText(list_chatmsg.get(position).getMsg_nickname());
        ((VH_chatmsg)holder).msg_content.setText(list_chatmsg.get(position).getMsg_content());
        Glide
                .with(context)
                .load(api_url.API_BASE_URL+"users/profile/"+list_chatmsg.get(position).getMsg_profileurl()).
                bitmapTransform(new CropCircleTransformation(context)).into(((VH_chatmsg) holder).img_profile);


        if(list_chatmsg.get(position).getFlag()==1){
            ((VH_chatmsg)holder).msg_nickname.setTextColor(Color.rgb(0,0,200));
            ((VH_chatmsg)holder).msg_content.setTextColor(Color.rgb(0,0,200));
        }
    }

    public class VH_chatmsg extends RecyclerView.ViewHolder{
        TextView msg_nickname;
        TextView msg_content;
        ImageView img_profile;

        public  VH_chatmsg(View view){
            super(view);
            msg_nickname=(TextView)view.findViewById(R.id.msg_nickname);
            msg_content=(TextView)view.findViewById(R.id.msg_content);
            img_profile=(ImageView)view.findViewById(R.id.img_chat_profile);
        }
    }

    public void addmsg(obj_chatmsg msg){
        list_chatmsg.add(msg);
    }
    public void addmsg_tolist(JSONObject msgobj){
        try {
            nwmsgobj = new obj_chatmsg(msgobj.getString("state"),msgobj.getString("nickname"),msgobj.getString("msg"),msgobj.getString("rmnum"));
            this.list_chatmsg.add(nwmsgobj);
            notifyDataSetChanged();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
