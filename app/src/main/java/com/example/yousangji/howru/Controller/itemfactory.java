package com.example.yousangji.howru.Controller;

import android.app.Activity;
import android.content.Context;

import com.example.yousangji.howru.Model.itm_assertvideo;
import com.example.yousangji.howru.Model.itm_basevideo;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;

import java.io.IOException;

/**
 * Created by YouSangJi on 2017-10-26.
 */

public class itemfactory {
    private Context mcontext;
    public static itm_basevideo createItemFromAsset(String assetName, int imageResource, Activity activity, VideoPlayerManager<MetaData> videoPlayerManager) throws IOException {
        return new itm_assertvideo(assetName, activity.getAssets().openFd(assetName), videoPlayerManager, imageResource,activity);
    }

    /*
    public static itm_basevideo createItemFromUrl(String assetName,String videourl, String imageResource, Activity activity, VideoPlayerManager<MetaData> videoPlayerManager) throws IOException {
        return new itm_direclinkvideo(assetName, videourl, videoPlayerManager, imageResource,activity);
    }*/


}
