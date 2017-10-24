package com.example.yousangji.howru.Model;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class obj_retrofit {


    protected static Object retrofit(Context context, Class<?> serviceName) {

        //Here a logging interceptor is created
        //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        //logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //httpClient.addInterceptor(logging);

        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://52.78.169.32/")
                .build();


        /**
         * 서비스객체의 이름으로 Retrofit 객체 생성 및 반환
         *
         * ex) retrofit.create(SignService.class);
         */
        return retrofit.create(serviceName);
    }
}
