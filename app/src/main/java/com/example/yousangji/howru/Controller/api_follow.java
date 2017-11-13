package com.example.yousangji.howru.Controller;

import android.content.Context;

import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_noti;
import com.example.yousangji.howru.Model.obj_retrofit;
import com.example.yousangji.howru.Model.obj_serverresponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by YouSangJi on 2017-11-01.
 */

public class api_follow extends obj_retrofit {
    public static followapi getRetrofit(Context context) {
        // 현재 서비스객체의 이름으로 Retrofit 객체를 초기화 하고 반환
        return (followapi) retrofit(context, followapi.class);
    }

    // SignAPI 인터페이스
    public interface followapi {
        /**
         * follow
         *
         * @param userid
         * @param followeeid
         * @param fcmmessage
         * @return
         */
        @FormUrlEncoded
        @POST(api_url.NOTIFY_FOLLOW)
        Call<obj_serverresponse> post(

                @Field("userid") String userid,
                @Field("followeeid") String followeeid,
                @Field("fcmmessage") String fcmmessage


        );

        /**
         * follow
         *
         * @param userid
         */
        @FormUrlEncoded
        @POST(api_url.NOTIFY_GET)
        Call<List<obj_noti>> get(

                @Field("userid") String userid
        );

        /**
         * follow
         *
         * @param userid
         * @param fcmmessage
         * @return
         */
        @FormUrlEncoded
        @POST(api_url.NOTIFY_SEND)
        Call<obj_serverresponse> send(

                @Field("userid") String userid,
                @Field("fcmmessage") String fcmmessage


        );
    }
}
