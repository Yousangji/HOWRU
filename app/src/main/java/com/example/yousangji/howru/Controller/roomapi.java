package com.example.yousangji.howru.Controller;

import android.content.Context;

import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_retrofit;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.Model.obj_serverresponse;

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

public class roomapi extends obj_retrofit {
    public static RoomApi getRetrofit(Context context) {
        // 현재 서비스객체의 이름으로 Retrofit 객체를 초기화 하고 반환
        return (RoomApi) retrofit(context, RoomApi.class);
    }

    // SignAPI 인터페이스
    public interface RoomApi {
        /**
         * 스트리밍등록 메소드
         * @param rmid
         * @param title
         * @param Streamerid
         * @param Streamernick

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.POST_STREAM)
        Call<obj_serverresponse> post(

                @Field("rmid") String rmid,
                @Field("rmtitle") String title,
                @Field("streamerid") String Streamerid,
                @Field("streamernick") String Streamernick

        );

        /**
         * 회원가입 메소드
         *
         * @param onair

         * @return
         */
        @FormUrlEncoded
        @POST(api_url.GET_STREAM_LIST)
        Call<List<obj_room>> get(
                @Field("onair") int onair

        );

        /**
         * 스트리밍 종료 메소드
         *
         * @param rmid
         * @return
         */
        @FormUrlEncoded
        @POST(api_url.UPDATE_STREAMFLAG)
        Call<obj_room> update(
                @Field("rmid") String rmid
        );

        /**
         * 스트리밍 종료 메소드
         *
         * @param pubdate
         * @return
         */
        @FormUrlEncoded
        @POST(api_url.UPDATE_STREAMFLAG)
        Call<obj_room> reget(
                @Field("pubdate") String pubdate
        );


        /**
         * 스트리밍 종료 메소드
         *
         * @param file
         * @param rmid
         * @return
         */
        @Multipart
        @POST(api_url.POST_VOD)
        Call<obj_serverresponse> uploadVideo(
                @Part MultipartBody.Part file,
                @Part("nickname") RequestBody rmid
        );

    }
}
