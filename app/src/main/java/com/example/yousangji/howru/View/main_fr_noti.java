package com.example.yousangji.howru.View;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yousangji.howru.Controller.adt_recy_noti;
import com.example.yousangji.howru.Controller.api_follow;
import com.example.yousangji.howru.Model.obj_noti;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by YouSangJi on 2017-11-02.
 */

public class main_fr_noti extends Fragment {
    RecyclerView recy_noti;
    adt_recy_noti notiadt;
    ProgressBar progressBar;
    TextView txt_noti;

    util_sharedpref preutil;
    String userid;
    String fcmmsg;
    private SwipeRefreshLayout lay_swpref;
    Handler hdlr_imcoming=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            fcmmsg = msg.getData().getString("msg");
            retro_getnoti();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.lay_fr_noti,container,false);
        recy_noti=(RecyclerView)rootview.findViewById(R.id.recy_noti);
        progressBar=(ProgressBar)rootview.findViewById(R.id.progbar_noti);
        txt_noti=(TextView)rootview.findViewById(R.id.txt_noti_none);
        lay_swpref=(SwipeRefreshLayout)rootview.findViewById(R.id.swp_notilist);
        //recyclerview setting
        recy_noti.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recy_noti.setLayoutManager(layoutManager);

        //shared
        util_sharedpref.createInstance(getApplicationContext());
        preutil=util_sharedpref.getInstance();
        userid=preutil.getString("userid");
        Log.d("mytag","[main_fr_noti] userid : "+userid);

        //adatersetting
        notiadt=new adt_recy_noti(getActivity(),userid);
        recy_noti.setAdapter(notiadt);

        //get objnoti
        retro_getnoti();

        lay_swpref.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retro_getnoti();
                lay_swpref.setRefreshing(false);
            }
        });
        return rootview;
    }

    public void retro_getnoti(){
        api_follow.getRetrofit(getApplicationContext()).get(userid).enqueue(new Callback<List<obj_noti>>() {
            @Override
            public void onResponse(Call<List<obj_noti>> call, Response<List<obj_noti>> response) {
                //네트워크 통신 완료
                progressBar.setVisibility(View.INVISIBLE);

                if(response.isSuccessful()) {
                    List<obj_noti>list_noti=response.body();
                    if(list_noti.isEmpty()){
                        txt_noti.setText(R.string.notinone);
                    }else {
                        txt_noti.setVisibility(View.GONE);
                        notiadt.putlist(list_noti);
                        notiadt.notifyDataSetChanged();
                    }
                }else{
                    Log.d("mytag","[retrofit response]"+response.toString());
                    Log.d("mytag","[retrofit response body]"+response.body());
                    Log.d("mytag","[retrofit response message]"+response.message());
                }
            }

            @Override
            public void onFailure(Call<List<obj_noti>> call, Throwable t) {
                //네트워크 통신 완료
                progressBar.setVisibility(View.GONE);
                Log.d("mytag","[main_fr_noti] get failure");
                t.printStackTrace();
            }
        });
    }
}
