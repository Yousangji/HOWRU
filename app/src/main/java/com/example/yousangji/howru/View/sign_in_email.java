package com.example.yousangji.howru.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.yousangji.howru.Controller.api_sign;
import com.example.yousangji.howru.Model.obj_user;
import com.example.yousangji.howru.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class sign_in_email extends AppCompatActivity {

    //Edit text
    EditText edit_email;
    EditText edit_password;
    //Layout
    TextInputLayout lay_email;
    TextInputLayout lay_password;
    //String
    String str_email;
    String str_password;
    //Button
    ImageButton btn_login_email;

    //


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_login_email);

        //Edit text
         edit_email=(EditText)findViewById(R.id.edit_login_email);
         edit_password=(EditText)findViewById(R.id.edit_login_password);
        //Layout
         lay_email=(TextInputLayout)findViewById(R.id.lay_login_email);
         lay_password=(TextInputLayout)findViewById(R.id.lay_login_pwd);
        //Button
        btn_login_email=(ImageButton)findViewById(R.id.btn_login_done);

        btn_login_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.GET emailaddr, password string
                str_email=edit_email.getText().toString();
                str_password=edit_password.getText().toString();


                //2. login retrofit 통신
               httplogin(str_email,str_password);

                //TODO:3. userobj 생성

                //TODO:4.sharedpreference 할당

                //5.Intent
                Intent inte_tomain=new Intent(sign_in_email.this,main_container.class);
                startActivity(inte_tomain);
            }
        });


    }

    public void httplogin(String str_email,String str_password){
        api_sign.getRetrofit(getApplicationContext()).post_signin(str_email,str_password).enqueue(new Callback<obj_user>() {
            @Override
            public void onResponse(Call<obj_user> call, Response<obj_user> response) {
                Log.d("mytag","[Http Login]onresponse");
            }

            @Override
            public void onFailure(Call<obj_user> call, Throwable t) {
                Log.d("mytag","[Http Login]on failure");

            }
        });
    }
}
