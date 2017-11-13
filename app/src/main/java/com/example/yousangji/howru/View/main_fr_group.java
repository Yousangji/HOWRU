package com.example.yousangji.howru.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yousangji.howru.Controller.adt_recy_video;
import com.example.yousangji.howru.Controller.roomapi;
import com.example.yousangji.howru.Model.itm_basevideo;
import com.example.yousangji.howru.Model.itm_direclinkvideo;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.R;
import com.volokh.danylo.video_player_manager.Config;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YouSangJi on 2017-10-26.
 */

public class main_fr_group extends Fragment {


    ProgressBar progressBar;
    TextView txt_none;
    SwipeRefreshLayout lay_swpref;
    SwipeRefreshLayout.OnRefreshListener onRefreshListene;

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = main_fr_group.class.getSimpleName();
    private adt_recy_video videoRecyclerViewAdapter;

    private  ArrayList<itm_basevideo> mList = new ArrayList<>();

    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */

    private  ListItemsVisibilityCalculator mVideoVisibilityCalculator= new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {
            Log.d("mytag","[main_fr_group]playeritem changed");
        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getstreamlist();

        View rootView = inflater.inflate(R.layout.lay_fr_group, container, false);

        progressBar=(ProgressBar)rootView.findViewById(R.id.progbar_group);
        txt_none=(TextView)rootView.findViewById(R.id.txt_group_none);
        lay_swpref=(SwipeRefreshLayout)rootView.findViewById(R.id.swp_videogroup);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d("mytag","[test]adapter 먼저임");
         videoRecyclerViewAdapter = new adt_recy_video(mVideoPlayerManager, getActivity());

        mRecyclerView.setAdapter(videoRecyclerViewAdapter);



        //Temp
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){

                    Log.d("mytag",String.valueOf(mLayoutManager.findFirstVisibleItemPosition()));
                    Log.d("mytag",String.valueOf(mLayoutManager.findFirstVisibleItemPosition()) );
                    Log.d("mytag",String.valueOf( mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1) );
                    mVideoVisibilityCalculator.onScroll(

                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });

        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

        onRefreshListene=new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getstreamlist();
                lay_swpref.setRefreshing(false);
            }
        };
        lay_swpref.setOnRefreshListener(onRefreshListene);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){

            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoPlayerManager.resetMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
       // mVideoPlayerManager.resetMediaPlayer();
    }


    public void getstreamlist(){

        //Stream list
        roomapi.getRetrofit(getContext()).get(0).enqueue(new Callback<List<obj_room>>() {
            @Override
            public void onResponse(Call<List<obj_room>> call, Response<List<obj_room>> response) {
                //네트워크 통신 완료
                progressBar.setVisibility(View.GONE);

                //Room obj 생성, adapter 추가
                Log.d("mytag","response: "+response.toString());
                List<obj_room> streamlist=response.body();
                if(!streamlist.isEmpty()) {

                    txt_none.setVisibility(View.GONE);
                    for (int i = 0; i < streamlist.size(); i++) {
                        mList.add(new itm_direclinkvideo(streamlist.get(i).getTitle(), streamlist.get(i).getRmpath(), mVideoPlayerManager, streamlist.get(i).getThumnailurl(), getActivity(),streamlist.get(i).getNickname(),streamlist.get(i).getProfileurl()));
                    }
                    videoRecyclerViewAdapter.addVideo(mList);
                    videoRecyclerViewAdapter.notifyDataSetChanged();


                    //mVideoVisibilityCalculator = new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);
                }else{
                    txt_none.setText(R.string.objnone);
                }





            }

            @Override
            public void onFailure(Call<List<obj_room>> call, Throwable t) {
                //네트워크 통신 완료
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Log.d("mytag","failure");
                txt_none.setText(R.string.networkerror);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
          //  onRefreshListene.onRefresh();
        }else{

        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
