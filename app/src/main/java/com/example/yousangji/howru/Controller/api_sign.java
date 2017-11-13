package com.example.yousangji.howru.Controller;

import android.content.Context;

import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_retrofit;
import com.example.yousangji.howru.Model.obj_serverresponse;
import com.example.yousangji.howru.Model.obj_user;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
         * @param userid
         * @param emailaddr
         * @param username
         * @param fcmtoken

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.POST_USERS)
        Call<obj_user> post_signup(

                @Field("userid") String userid,
                @Field("emailaddr") String emailaddr,
                @Field("username") String username,
                @Field("fcmtoken") String fcmtoken

        );

        /**
         * 로그인-email
         *
         * @param emailaddr
         * @param password

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.GET_STREAM_LIST)
        Call<obj_user> post_signin(
                @Field("emailaddr") String emailaddr,
                @Field("usrpassword") String password

        );

        /**
         * @param userid
         * @return
         * */
        @FormUrlEncoded
        @POST(api_url.GET_USERS)
        Call<List<obj_user>> get_follow(
                @Field("userid") String userid
        );


        /**
         * @param nickname
         * @param usermsg
         * @param userid
         * @param profileurl
         * @return
         * */
        @FormUrlEncoded
        @POST(api_url.PUT_USERS)
        Call<obj_serverresponse> put(
                @Field("nickname") String nickname,
                @Field("usermsg") String usermsg,
                @Field("userid") String userid,
                @Field("profileurl") String profileurl

        );

        /**
         * @param nickname
         * @param usermsg
         * @param userid

         * @return
         * */
        @Multipart
        @POST(api_url.PUT_USERSWITHIMG)
        Call<obj_serverresponse> putwithimg(
                @Part MultipartBody.Part file,
                @Part("nickname") RequestBody nickname,
                @Part("usermsg") RequestBody usermsg,
                @Part("userid") RequestBody userid
        );

    }
}
