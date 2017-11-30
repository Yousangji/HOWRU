package com.example.yousangji.howru.View;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yousangji.howru.Controller.adt_card_room;
import com.example.yousangji.howru.Controller.roomapi;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class main_fr_home extends Fragment{

    private TextView txt_noti;
    private ProgressBar progressBar;
    private ImageButton btn_refresh_home;
    private SwipeRefreshLayout lay_swpref;
    private RecyclerView recyclerView;
    private adt_card_room adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<obj_room> obj_roomList;
    private String category;


    public static Fragment newInstance(String param1) {
        main_fr_home fragment = new main_fr_home();
        Bundle args = new Bundle();
        args.putString("category", param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            category = getArguments().getString("category");
            Log.d("mytag","[mainfrhome]category"+category);
        }else{
            Log.d("mytag","[mainfrhome] argument null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.lay_frm_mainhome,container,false);

        btn_refresh_home=(ImageButton)rootview.findViewById(R.id.btn_refresh_home);
        progressBar=(ProgressBar)rootview.findViewById(R.id.progbar_home);
        txt_noti=(TextView)rootview.findViewById(R.id.txt_home_none);
        lay_swpref=(SwipeRefreshLayout)rootview.findViewById(R.id.swp_streamlist);
        obj_roomList = new ArrayList<>();
        recyclerView=(RecyclerView)rootview.findViewById(R.id.card_recy);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new adt_card_room(getActivity(), obj_roomList);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(15), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //preparecard();

        getstreamlist();


        lay_swpref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getstreamlist();
                lay_swpref.setRefreshing(false);
            }
        });

        btn_refresh_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getstreamlist();
                lay_swpref.setRefreshing(false);
            }
        });

        return rootview;
    }

    public void getstreamlist(){
       if(isConnected()) {
           //Stream list
           roomapi.getRetrofit(getContext()).get(0, category).enqueue(new Callback<List<obj_room>>() {
               @Override
               public void onResponse(Call<List<obj_room>> call, Response<List<obj_room>> response) {
                   progressBar.setVisibility(View.GONE);
                   btn_refresh_home.setVisibility(View.GONE);
                   Log.d("mytag", "response: " + response.toString() + "responsemessage:" + response.message());
                   List<obj_room> streamlist = response.body();
                   adapter.setlist(streamlist);

               }

               @Override
               public void onFailure(Call<List<obj_room>> call, Throwable t) {
                   progressBar.setVisibility(View.GONE);
                   btn_refresh_home.setVisibility(View.GONE);
                   txt_noti.setText("라이브 중인 방이 없어요. 첫 BJ가 되어주세요");
                   t.printStackTrace();
                   Log.d("mytag", "failure" + t.getCause());
               }
           });
       }else{
           btn_refresh_home.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.GONE);
           txt_noti.setText("네트워크 연결이 끊어져있어요. 네트워크를 연결한 후 새로고침을 눌러주세요.");
       }
    }

    public boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetworkinfo=cm.getActiveNetworkInfo();
        if(activenetworkinfo==null){
            return false;
        }
        return true;

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
