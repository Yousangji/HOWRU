package com.example.yousangji.howru.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yousangji.howru.R;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * Created by YouSangJi on 2017-10-26.
 */

public class holder_videoview extends  RecyclerView.ViewHolder{
    public final VideoPlayerView mPlayer;
    public final TextView mTitle;
    public final ImageView mCover;
    public final TextView mVisibilityPercents;
    public final ImageView video_userprofile;
    public final TextView video_nickname;


    public holder_videoview(View view) {
        super(view);
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
        mTitle = (TextView) view.findViewById(R.id.txt_card_time);
        mCover = (ImageView) view.findViewById(R.id.cover);
        mVisibilityPercents = (TextView) view.findViewById(R.id.visibility_percents);
        video_userprofile=(ImageView)view.findViewById(R.id.img_video_userprofile);
        video_nickname=(TextView) view.findViewById(R.id.txt_video_username);
    }
}
