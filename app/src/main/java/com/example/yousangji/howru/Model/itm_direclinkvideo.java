package com.example.yousangji.howru.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.yousangji.howru.View.holder_videoview;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by YouSangJi on 2017-10-26.
 */

public class itm_direclinkvideo extends itm_basevideo {
    private final String mDirectUrl;
    private final String mTitle;
    private String nickname;
    private String url_profile;



    private final String mImageResource;
    private Context mcontext;

    public itm_direclinkvideo(String title, String directUr, VideoPlayerManager videoPlayerManager, String imageResource, Context context,String n,String up) {
        super(videoPlayerManager);
        mDirectUrl = api_url.API_BASE_URL+directUr;
        mTitle = title;
        mImageResource = api_url.API_BASE_URL+imageResource;
        mcontext=context;
        nickname=n;
        url_profile=up;

        Log.d("mytag","[itm_direct_video]directurl: "+mDirectUrl+"imageurl: "+mImageResource);

    }

    @Override
    public void update(int position, holder_videoview viewHolder, VideoPlayerManager videoPlayerManager) {
        viewHolder.mTitle.setText(mTitle);
        viewHolder.mCover.setVisibility(View.VISIBLE);
        viewHolder.video_nickname.setText(nickname);

        Glide.with(mcontext).load(api_url.API_BASE_URL+"users/profile/"+url_profile).bitmapTransform(new CropCircleTransformation(mcontext)).override(30,30).into(viewHolder.video_userprofile);

        Glide.with(mcontext).load(mImageResource).into(viewHolder.mCover);
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl);
    }

    @Override
    public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }
}
