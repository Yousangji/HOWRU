package com.example.yousangji.howru.Controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.yousangji.howru.Model.itm_basevideo;
import com.example.yousangji.howru.View.holder_videoview;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YouSangJi on 2017-10-26.
 */

public class adt_recy_video  extends RecyclerView.Adapter<holder_videoview>{
    private final VideoPlayerManager mVideoPlayerManager;
    public  List<itm_basevideo> mList=new ArrayList<>();
    private final Context mContext;

    public adt_recy_video(VideoPlayerManager videoPlayerManager, Context context){
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;

    }

    @Override
    public holder_videoview onCreateViewHolder(ViewGroup viewGroup, int position) {
        itm_basevideo videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup, mContext.getResources().getDisplayMetrics().widthPixels);
        return new holder_videoview(resultView);
    }

    @Override
    public void onBindViewHolder(holder_videoview viewHolder, int position) {
        itm_basevideo videoItem = mList.get(position);
        videoItem.update(position, viewHolder, mVideoPlayerManager);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addVideo(List<itm_basevideo> itm){
        mList=itm;
        notifyDataSetChanged();
    }
}
