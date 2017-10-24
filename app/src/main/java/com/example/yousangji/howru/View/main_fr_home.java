package com.example.yousangji.howru.View;


import android.content.res.Resources;
import android.graphics.Rect;
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

    private SwipeRefreshLayout lay_swpref;
    private RecyclerView recyclerView;
    private adt_card_room adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<obj_room> obj_roomList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.lay_frm_mainhome,container,false);


        lay_swpref=(SwipeRefreshLayout)rootview.findViewById(R.id.swp_streamlist);
        obj_roomList = new ArrayList<>();
        recyclerView=(RecyclerView)rootview.findViewById(R.id.card_recy);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new adt_card_room(getActivity(), obj_roomList);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
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

        return rootview;
    }

    public void getstreamlist(){
        //Stream list
        roomapi.getRetrofit(getContext()).get(0).enqueue(new Callback<List<obj_room>>() {
            @Override
            public void onResponse(Call<List<obj_room>> call, Response<List<obj_room>> response) {
                Log.d("mytag","response: "+response.toString());
                List<obj_room> streamlist=response.body();
                adapter.setlist(streamlist);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<obj_room>> call, Throwable t) {
                t.printStackTrace();
                Log.d("mytag","failure");
            }
        });
    }

    public void refresh(){



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
