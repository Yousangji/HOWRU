package com.example.yousangji.howru.Controller;

import android.content.Context;

import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_retrofit;
import com.example.yousangji.howru.Model.obj_user;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class api_sign extends obj_retrofit {
    public static signapi getRetrofit(Context context) {
        // 현재 서비스객체의 이름으로 Retrofit 객체를 초기화 하고 반환
        return (signapi) retrofit(context, signapi.class);
    }

    // SignAPI 인터페이스
    public interface signapi {
        /**
         * 회원가입 메소드
         * @param emailaddr
         * @param usrname
         * @param usrpassword
         * @param usrsex

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.POST_STREAM)
        Call<obj_user> post_signup(

                @Field("emailaddr") String emailaddr,
                @Field("usrname") String usrname,
                @Field("usrpassword") String usrpassword,
                @Field("usrsex") String usrsex

        );

        /**
         * 로그인-email
         *
         * @param emailaddr
         * @param usrpassword

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.GET_STREAM_LIST)
        Call<obj_user> post_signin(
                @Field("emailaddr") String emailaddr,
                @Field("usrpassword") String usrpassword

        );


    }
}
