package com.example.yousangji.howru.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.yousangji.howru.R;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class sign_in_sns extends AppCompatActivity{
   //input string userinfo
    String emailadress;
    String password;

    // get String userinfo
    String username;


    //ImageView
    ImageView btn_login_email;
    ImageView btn_login_faceb;
    ImageView btn_login_google;
    ImageView btn_login_kakao;
    //dialog_email
    Dialog d;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_login_sns);

        btn_login_email=(ImageView)findViewById(R.id.btn_login_email);
        btn_login_faceb=(ImageView)findViewById(R.id.btn_login_facebook);
        btn_login_google=(ImageView)findViewById(R.id.btn_login_google);

        //1. email 로그인/회원가입
        btn_login_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1. bottom dialog
                View view_dialog = getLayoutInflater().inflate(R.layout.lay_itm_signdialog, null);
                d = new BottomSheetDialog(sign_in_sns.this);
                d.setContentView(view_dialog);
                d.setCancelable(true);
                d.show();

                Button btn_dialog_signup=view_dialog.findViewById(R.id.btn_dialog_signup);
                Button btn_dialog_singin=view_dialog.findViewById(R.id.btn_dialog_signin);

                // 1-1. 회원가입 or 로그인 확인 => Intent
                btn_dialog_signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        Intent tosignup=new Intent(sign_in_sns.this,sign_up.class);
                        startActivity(tosignup);
                    }
                });

                btn_dialog_singin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                        Intent tosignin=new Intent(sign_in_sns.this,sign_in_email.class);
                        startActivity(tosignin);
                    }
                });

            }
        });

        //TODO:2.SNS 로그인

        //TODO:2-1  login retrofit 통신: 회원가입 확인

        //TODO:2-2  로그인



        //TODO:2.2.1. userobj 생성

        //TODO:2.2.2.sharedpreference 할당

        //2.2.3.Intent
        Intent inte_tomain=new Intent(sign_in_sns.this,main_container.class);
        startActivity(inte_tomain);



    }
}
