package com.example.yousangji.howru.View;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.yousangji.howru.Controller.api_sign;
import com.example.yousangji.howru.Model.obj_user;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class sign_in_sns extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    //Activity info
    static String tag="mytag";
   //input string userinfo
    String emailadress;
    String password;

    // get String userinfo
    String username;
    String userid=null;


    //ImageView
    ImageView btn_login_email;
    ImageView btn_login_faceb;
    ImageView btn_login_google;
    ImageView btn_login_kakao;
    VideoView videoView_background;
    Button btn_logout;
    //dialog_email
    Dialog d;

    //facebook
    private CallbackManager cbmng_login_faceb;
    private GoogleApiClient mGoogleApiClient;

    private int rc_signin_google=1000;

    //
     util_sharedpref prefutil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_login_sns);
        videoView_background=(VideoView)findViewById(R.id.videoview_login);
        btn_login_email=(ImageView)findViewById(R.id.btn_login_email);
        btn_login_faceb=(ImageView)findViewById(R.id.btn_login_facebook);
        btn_login_google=(ImageView)findViewById(R.id.btn_login_google);
        btn_logout=(Button)findViewById(R.id.btn_logout);

        ///Video background


        Uri uri=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.explore);
        videoView_background.setVideoURI(uri);
        videoView_background.start();
        //TODO:로그인 상태 확인, fcm 정보 전달
        //shared
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
        userid=prefutil.getString("userid",null);
        Log.d("mytag","[list_follow] userid : "+userid);

        /*//TODO:자동로그인
        if(userid!=null){
            Intent autologin =new Intent(sign_in_sns.this,main_container.class);
            startActivity(autologin);
        }*/

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


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isnetworkavailable()) {
                    Intent in_signin_google = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(in_signin_google, rc_signin_google);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(sign_in_sns.this);
                    builder.setTitle("네트워크 연결");
                    builder.setCancelable(true);
                    builder.setMessage("네트워크 연결이 되어있지 않아요. 네트워크 연결 후 다시 실행해주세요");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("mytag","[sns_login]"+requestCode);
        if (requestCode == rc_signin_google) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //handle google login result

            Log.d(tag, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                emailadress=acct.getEmail();
                username=acct.getDisplayName();
                userid=acct.getId();

                Log.d("mytag","[sign_in_sns]email,username,userid"+emailadress+username+userid);

                retro_signup(userid,emailadress,username);
            }else{
                // Google sign failed
            }

        }else{
            cbmng_login_faceb.onActivityResult(requestCode, resultCode, data);

        }



    }

    public void onclicklistenerfaceb(View v){
        if(isnetworkavailable()){
        //facebook initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        cbmng_login_faceb= CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(sign_in_sns.this, Arrays.asList("public_profile","email"));
        LoginManager.getInstance().registerCallback(cbmng_login_faceb, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request;
                request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {
                            Log.e("mytag", response.getError().toString());
                        } else {
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + loginResult.getAccessToken().getToken());
                            setResult(RESULT_OK);
                            try {
                                userid = user.getString("id");
                                emailadress = user.getString("email");
                                username = user.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //TODO: DB등록
                            retro_signup(userid, emailadress, username);

                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("mytag", "Error: " + error);

            }
        });

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(sign_in_sns.this);
            builder.setTitle("네트워크 연결");
            builder.setCancelable(true);
            builder.setMessage("네트워크 연결이 되어있지 않아요. 네트워크 연결 후 다시 실행해주세요");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    public void retro_signup(String i,String e,String n){
        String t= FirebaseInstanceId.getInstance().getToken();
        Log.d("mytag","[sign_in_sns]fcmtoken"+t);
        api_sign.getRetrofit(getApplicationContext()).post_signup(i,e,n,t).enqueue(new Callback<obj_user>() {
            @Override
            public void onResponse(Call<obj_user> call, Response<obj_user> response) {
                Log.d("mytag","[Http signup] "+response.message());
                if(response.isSuccessful()) {
                    //TODO:2.2.1. userobj 생성
                    obj_user userobj = response.body();
                    Gson gson = new Gson();
                    String userobj_str = gson.toJson(userobj);


                    Log.d("mytag", tag + "[Gson obj to json]" + userobj_str);

                    //TODO:2.2.2.sharedpreference 할당

                    util_sharedpref.createInstance(getApplicationContext());
                    prefutil=util_sharedpref.getInstance();
                    prefutil.putString("userinfo", userobj_str);
                    prefutil.putString("userid",userobj.getUserid());
                    prefutil.putString("followee",userobj.getFollowee());
                    prefutil.putString("username",userobj.getUsername());
                    prefutil.putString("nickname",userobj.getNickname());
                    prefutil.putString("profileurl",userobj.getProfileurl());
                    prefutil.putString("usermsg",userobj.getUsermsg());
                    Log.d("mytag", tag + "[put sharedpreference]" + userobj_str);

                    Toast.makeText(sign_in_sns.this, "로그인되었습니다", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(sign_in_sns.this, main_container.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.d("mytag","[retrofit response]"+response.toString());
                    Log.d("mytag","[retrofit response body]"+response.body());
                    Log.d("mytag","[retrofit response message]"+response.message());
                }
            }

            @Override
            public void onFailure(Call<obj_user> call, Throwable t) {
                Log.d("mytag","[Http signup] failure");
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("mytag","[Google_Signin] onconnection failed");
    }

    public boolean isnetworkavailable(){
        ConnectivityManager cm=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetworkinfo=cm.getActiveNetworkInfo();
        if(activenetworkinfo==null){
            return false;
        }
        return true;
    }
}
