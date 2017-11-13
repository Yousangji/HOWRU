package com.example.yousangji.howru.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yousangji.howru.Controller.adt_recy_follow;
import com.example.yousangji.howru.Controller.api_sign;
import com.example.yousangji.howru.Model.obj_user;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by YouSangJi on 2017-10-31.
 */

public class main_fr_follow extends Fragment {
    RecyclerView recy_list;
    adt_recy_follow follow_adt;
    String userid;
    String followee;
    util_sharedpref prefutil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.lay_listfriend,container,false);
        recy_list=(RecyclerView) rootview.findViewById(R.id.recy_follow);

        //리스트 recyclerview
        recy_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recy_list.setLayoutManager(layoutManager);

        //shared
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
         userid=prefutil.getString("userid");
        followee=prefutil.getString("followee");
        Log.d("mytag","[list_follow] userid : "+userid);

        //adpater
        //받은 리스트를 recyclerview에 adapt
        follow_adt = new adt_recy_follow(getActivity(),userid,followee);
        recy_list.setAdapter(follow_adt);



        //get userobjs
        retro_getusers();

        return rootview;
    }



    public void retro_getusers(){
        api_sign.getRetrofit(getApplicationContext()).get_follow(userid).enqueue(new Callback<List<obj_user>>() {
            @Override
            public void onResponse(Call<List<obj_user>> call, Response<List<obj_user>> response) {
                Log.d("mytag","[list_follow] "+response.message());
                if(response.isSuccessful()) {

                    //TODO:2.2.1. userobj 생성

                    List<obj_user>list_users=response.body();
                    follow_adt.putlist(list_users);
                    follow_adt.notifyDataSetChanged();

                }else{
                    Log.d("mytag","[retrofit response]"+response.toString());
                    Log.d("mytag","[retrofit response body]"+response.body());
                    Log.d("mytag","[retrofit response message]"+response.message());
                }
            }

            @Override
            public void onFailure(Call<List<obj_user>> call, Throwable t) {
                Log.d("mytag","[Http signup] failure");
            }
        });
    }
}
