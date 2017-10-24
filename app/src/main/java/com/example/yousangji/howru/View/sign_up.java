package com.example.yousangji.howru.View;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yousangji.howru.Controller.api_sign;
import com.example.yousangji.howru.Model.obj_user;
import com.example.yousangji.howru.R;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class sign_up extends AppCompatActivity{
    Context context;

    // Edittext
    EditText inputemail;
    EditText inputpassword;
    EditText inputpassword_re;
    EditText inputname;

    //layout
    TextInputLayout email_lay;
    TextInputLayout pwd_lay;
    TextInputLayout re_pwd_lay;
    TextInputLayout name_lay;


    TextView emailck;
    Button btn_regis;


    String emailadress;
    String username;
    String password;
    String passwordcheck;

    Retrofit retrofit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_signup);
        getIntent();

        //set component
        inputemail=(EditText)findViewById(R.id.setemail);
        inputpassword=(EditText)findViewById(R.id.setpassword);
        inputpassword_re=(EditText)findViewById(R.id.passwordcheck);
        inputname=(EditText)findViewById(R.id.setusername);
        email_lay=(TextInputLayout)findViewById(R.id.reg_email_lay);
        pwd_lay=(TextInputLayout)findViewById(R.id.reg_pwd_lay);
        re_pwd_lay=(TextInputLayout)findViewById(R.id.reg_repwd_lay);
        name_lay=(TextInputLayout)findViewById(R.id.reg_name_lay);
        btn_regis=(Button)findViewById(R.id.btn_Signup);

        //join 버튼 클릭시
        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //유효성 검사
                if(validate()){
                    //1. Post emailaddr, password, name, sex
                    retro_signup();

                    //TODO:2. if :emailaddr 중복체크

                    //TODO:2-1 true : intent

                    //TODO:2-2 false : textlayout 오류 안내 후 return


                }


            }
        });//oncreate
    }

    //http sign_up 통신
    public void retro_signup(){
        api_sign.getRetrofit(getApplicationContext()).post_signup(emailadress,username,password,"0").enqueue(new Callback<obj_user>() {
            @Override
            public void onResponse(Call<obj_user> call, Response<obj_user> response) {
                Log.d("mytag","[Http signup] "+response.message());

            }

            @Override
            public void onFailure(Call<obj_user> call, Throwable t) {
                Log.d("mytag","[Http signup] failure");
            }
        });

    }

    //유효성 검사
    private boolean validate(){
        boolean result=true;

        emailadress=inputemail.getText().toString();
        username=inputname.getText().toString();
        password=inputpassword.getText().toString();
        passwordcheck=inputpassword_re.getText().toString();

        //빈칸
        if(emailadress.equals("")){
            name_lay.setError(getString(R.string.text_error_message_blank));
            result=false;
        }

        if(username.equals("")){
            name_lay.setError(getString(R.string.text_error_message_blank));
            result=false;
        }

        if(password.equals("")){
            pwd_lay.setError(getString(R.string.text_error_message_blank));
            result=false;
        }

        if(passwordcheck.equals("")){
            re_pwd_lay.setError(getString(R.string.text_error_message_blank));
            result=false;
        }

        //이메일형식체크
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailadress).matches()) {
            email_lay.setError("이메일 형식이 아닙니다.");
            result=false;
        }

        //비밀번호 유효성
        if (!Pattern.matches("^(?=.*\\d)(?=.*\\W)(?=.*[a-zA-Z]).{8,20}$", password)) {
            pwd_lay.setError("비밀번호 형식을 지켜주세요");
            result=false;
        }
/*
        //핸드폰번호 유효성
        if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", mphonenumber)) {
            Toast.makeText(register.this, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        //비밀번호 확인
        if (!password.equals(passwordcheck)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(800);
            re_pwd_lay.setError("비밀번호가 같지 않습니다.");
            result=false;
        }

        return result;
    }





}
