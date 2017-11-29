package com.example.yousangji.howru.View;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yousangji.howru.Controller.Network;
import com.example.yousangji.howru.Controller.adt_recy_chat;
import com.example.yousangji.howru.Controller.adt_recy_filter;
import com.example.yousangji.howru.Controller.adt_spinner;
import com.example.yousangji.howru.Controller.api_follow;
import com.example.yousangji.howru.Controller.callback_filter;
import com.example.yousangji.howru.Controller.roomapi;
import com.example.yousangji.howru.Model.obj_chatmsg;
import com.example.yousangji.howru.Model.obj_filter_thumb;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.Model.obj_serverresponse;
import com.example.yousangji.howru.Model.thr_nettycli;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;
import com.github.faucamp.simplertmp.RtmpHandler;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.seu.magicfilter.utils.MagicFilterType;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class broadcaster extends AppCompatActivity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener,callback_filter {

    private static final String TAG = "[broadcaster]";

    //spinner
    private Spinner spinner_category;

    private String[] value_category={"수다방","먹방","놀방","액팅방","공방","기타"};
    private Spinner spinner_privacy;
    private String[] value_privacy={"전체 공개","팔로워","비공개"};
    private adt_spinner adt_spinner_category;
    private ArrayAdapter<String>  adt_spinner_privacy;
    private ImageView img_privacy;

    private Button btnPublish;
    private ImageView btnSwitchCamera;
    private Button btnRecord;
    private ImageView btnSwitchEncoder;
    private ImageButton btn_chat_show;
    private LinearLayout lay_chat_pub;
    private LinearLayout lay_btn_pub;
    private ImageButton btnfacetracker;




    private SharedPreferences sp;
    private String rtmpUrl = "rtmp://ossrs.net/" + getRandomAlphaString(3) + '/' + getRandomAlphaDigitString(5);
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";

    private custompublisher mPublisher;

    //tempo
    private String rminfo;
    private obj_room rmobj;

    //userinfo
    private util_sharedpref prefutil;
    private String userid;
    private String usernick;
    private String url_profil;
    private String rmtitle;
    private String rmid;

    //thumbnail
    private RecyclerView recy_filter;
    private ImageButton btn_filter;

    //chat
    Network networkreceiver;
    private RecyclerView chatmst_list;
    private RecyclerView.LayoutManager chat_layman;
    private adt_recy_chat chat_recyadapter;
    private ImageView btnchat_favorite;
    private EditText chat_edit;
    private ImageView btnchat_sbm;
    obj_chatmsg msgobj;
    private String str_msgobj=null;
    obj_chatmsg msgobj_received;
    private String str_msgobj_rev;
    Gson gson=new Gson();
    //chatServer_info
    private String ipaddress="52.78.169.32";
    private int Port=7777;

    //private clientSocketchannel socketchannel_obj;
    private thr_nettycli client;
    private String m_msgcontent=null;

    //TODO:temp

    private customcameraview preview;
    private GraphicOverlay mGraphicOverlay;
    private boolean flagfacetrack=false;
    private boolean isbroadcasting=false;

    private Handler m_Handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0: // 소켓 생성 완료
                    // 토스트
                    Toast.makeText(broadcaster.this, "socket setting", Toast.LENGTH_SHORT).show();

                    if(!isbroadcasting){
                    //set msgobj_default
                    msgobj=new obj_chatmsg();
                    msgobj.setMsg_state("0");
                    msgobj.setMsg_rmnum(rmid);
                    msgobj.setMsg_userid(userid);
                    msgobj.setMsg_nickname(usernick);
                    msgobj.setMsg_profileurl(url_profil);
                    msgobj.setMsg_content(usernick+"님이 방송을 시작하셨습니다.");
                    msgobj.setFlag(1);


                    str_msgobj=gson.toJson(msgobj);
                    client.sendMsg(str_msgobj);
                    Log.d("mytag","[broadcaster] sendsettingmsg" +str_msgobj);

                    chat_recyadapter.addmsg(msgobj);
                    chat_recyadapter.notifyDataSetChanged();
                    isbroadcasting=true;
                    }else{
                        Toast.makeText(broadcaster.this, "방송이 재시작 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1: // 데이터 수신 완료
                    // 수신 데이터 토스트로 띄움.
                    Toast.makeText(broadcaster.this, "server responded : " + msg.obj, Toast.LENGTH_SHORT).show();
                        //Json으로 받은 데이터를 list에 추가
                        Gson gson=new Gson();
                        msgobj_received=gson.fromJson(msg.obj.toString(),obj_chatmsg.class);
                        chat_recyadapter.addmsg(msgobj_received);
                        chat_recyadapter.notifyDataSetChanged();
                        Log.d("mytag","[broadcaster]received msg : "+msg.obj.toString());

                    break;

                case 2:

                    //set_chat Socket-(소켓 생성 및 read thread 생성)
                    client = new thr_nettycli(ipaddress, 8007, m_Handler);
                    client.start();
                    Log.d("mytag", "client 생성");
                    Toast.makeText(broadcaster.this, "방송에 재연결합니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    client.closesocket();
                    Toast.makeText(broadcaster.this, "네트워크 연결이 종료 되었습니다. 네트워크 연결시 방송에 재연결합니다.", Toast.LENGTH_SHORT).show();
                    break;

                case -1:
                    Log.d("mytag","[viewer]message return from server OK");
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //네트워크 Check 등록
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
         networkreceiver=new Network(m_Handler);
        registerReceiver(networkreceiver,filter);


        //roomobj 생성
        rmobj=new obj_room();

        //GET userinfo from SharedPreference
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
        String userinfostr=prefutil.getString("userinfo");
        usernick=prefutil.getString("nickname");
        url_profil=prefutil.getString("profileurl");
        try {
            JSONObject userobj = new JSONObject(userinfostr);
            userid = userobj.getString("userid");
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("mytag","[braodcaster] userid : "+userid+"usernickname : "+ usernick+" urlprofile : "+ url_profil);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.lay_publisher);

        // response screen rotation event
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        // restore data.
        sp = getSharedPreferences("Yasea", MODE_PRIVATE);
        rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);



        //edittext init
        final EditText edittitle=(EditText)findViewById(R.id.title);
        btnPublish = (Button) findViewById(R.id.publish);
        btnSwitchCamera = (ImageView) findViewById(R.id.swCam);
        btnRecord = (Button) findViewById(R.id.record);
        btnSwitchEncoder = (ImageView) findViewById(R.id.swEnc);
        btn_filter=(ImageButton)findViewById(R.id.btn_filter);
        btn_chat_show=(ImageButton)findViewById(R.id.btn_chat_pub);
        lay_btn_pub=(LinearLayout)findViewById(R.id.lay_btn_pub);

        ////******************Chat****************************************
        //set_chat component
        chatmst_list=(RecyclerView) findViewById(R.id.recy_chat_pub);
        btnchat_favorite=(ImageView)findViewById(R.id.btnchat_show);
        chat_edit=(EditText)findViewById(R.id.edit_chat_pub);
        btnchat_sbm=(ImageView) findViewById(R.id.btn_chat_sbm_pub);
        lay_chat_pub=(LinearLayout)findViewById(R.id.lay_chat_pub);


        //set_chat recyclerview
        chatmst_list.setHasFixedSize(true);
        chat_layman=new LinearLayoutManager(this);
        chatmst_list.setLayoutManager(chat_layman);
        chat_recyadapter=new adt_recy_chat(this);
        chatmst_list.setAdapter(chat_recyadapter);
       //********************************************************************


        /////////////////////////////////////////////////////////////////////
        //TODO:need to remove or fix
        btnfacetracker=(ImageButton)findViewById(R.id.btn_facetracker);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        /////////////////////////////////////////////////////////////////////

        //********************Spinner component/********************
        spinner_category=(Spinner)findViewById(R.id.spinner_category_pub);
        spinner_privacy=(Spinner)findViewById(R.id.spinner_open_pub);
        img_privacy=(ImageView)findViewById(R.id.img_public_pub);

        //set category spinner
        adt_spinner_category= new adt_spinner(broadcaster.this,android.R.layout.simple_list_item_1);
        adt_spinner_category.addAll(value_category);
        adt_spinner_category.add("카테고리");
        spinner_category.setAdapter(adt_spinner_category);
        spinner_category.setSelection(adt_spinner_category.getCount());
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(broadcaster.this, "selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //set privacy spinner
        adt_spinner_privacy=new ArrayAdapter<String>(broadcaster.this,android.R.layout.simple_spinner_dropdown_item,value_privacy);
        spinner_privacy.setAdapter(adt_spinner_privacy);
        spinner_privacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        img_privacy.setImageResource(R.drawable.world);
                        break;
                    case 1:
                        img_privacy.setImageResource(R.drawable.group);
                        break;
                    case 2:
                        img_privacy.setImageResource(R.drawable.lock);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //********************************************************************

        btnfacetracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startfacetrack();
            }
        });


        btn_chat_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lay_chat_pub.getVisibility()==View.GONE) {
                    lay_chat_pub.setVisibility(View.VISIBLE);
                    lay_btn_pub.setVisibility(View.GONE);
                }
            }
        });


        btnchat_sbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add Msg To Recyclerview & Send Msg
                m_msgcontent=chat_edit.getText().toString();
                msgobj=new obj_chatmsg();
                msgobj.setMsg_rmnum(rmid);
                msgobj.setMsg_userid(userid);
                msgobj.setMsg_nickname(usernick);
                msgobj.setMsg_profileurl(url_profil);
                msgobj.setMsg_state("3");
                msgobj.setMsg_content(m_msgcontent);
                msgobj.setFlag(1);
                chat_recyadapter.addmsg(msgobj);
                chat_recyadapter.notifyDataSetChanged();
                str_msgobj=gson.toJson(msgobj);
                client.sendMsg(str_msgobj);
                Log.d("mytag","[broadcaster] sendmsg" +str_msgobj);
                chat_edit.setText("");
            }
        });

        preview=(customcameraview)findViewById(R.id.glsurfaceview_camera);


        mPublisher = new custompublisher(preview);
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.switchCameraFilter(MagicFilterType.NONE);
        mPublisher.setPreviewResolution(640, 360);
        mPublisher.setOutputResolution(360, 640);
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();

        initfilterwidgets();

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recy_filter.getVisibility()==View.VISIBLE) {
                    recy_filter.setVisibility(View.GONE);
                }else{
                    recy_filter.setVisibility(View.VISIBLE);
                }
            }
        });
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPublish.getText().toString().contentEquals("방송하기")) {

                    //TODO: GET userobj info from sharedpref
                    //userid=edituserid.getText().toString();
                    //usernick=userid;
                    rmtitle=edittitle.getText().toString();
                    SimpleDateFormat s = new SimpleDateFormat("yyMMddhhmmss");
                    rmid = s.format(new Date())+userid;


                    Log.d("mytag","publish rmid : "+rmid);
                    rtmpUrl="rtmp://52.78.169.32:1935/show/"+rmid;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("rtmpUrl", rtmpUrl);
                    editor.apply();
                    Log.d("mytag","publishing rtmpurl : "+rtmpUrl);
                    mPublisher.startPublish(rtmpUrl);
                    mPublisher.startCamera();

                    /*
                    if (btnSwitchEncoder.getText().toString().contentEquals("soft encoder")) {
                        Toast.makeText(getApplicationContext(), "Use hard encoder", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Use soft encoder", Toast.LENGTH_SHORT).show();
                    }*/
                    btnPublish.setText("stop");
                    btnSwitchEncoder.setEnabled(false);

                } else if (btnPublish.getText().toString().contentEquals("stop")) {
                    mPublisher.stopPublish();
                    mPublisher.stopRecord();
                    btnPublish.setText("방송하기");
                    btnRecord.setText("record");
                    btnSwitchEncoder.setEnabled(true);

                    //방 종료 메시지
                    msgobj=new obj_chatmsg();
                    msgobj.setMsg_rmnum(rmid);
                    msgobj.setMsg_userid(userid);
                    msgobj.setMsg_nickname(usernick);
                    msgobj.setMsg_profileurl(url_profil);
                    msgobj.setMsg_state("1");
                    msgobj.setMsg_content(usernick+"님이 방을 종료합니다.");
                    chat_recyadapter.addmsg(msgobj);
                    chat_recyadapter.notifyDataSetChanged();
                    str_msgobj=gson.toJson(msgobj);
                    client.sendMsg(str_msgobj);
                }
            }
        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btnRecord.getText().toString().contentEquals("record")) {
                    if (mPublisher.startRecord(recPath)) {
                        btnRecord.setText("pause");
                    }
                } else if (btnRecord.getText().toString().contentEquals("pause")) {
                    mPublisher.pauseRecord();
                    btnRecord.setText("resume");
                } else if (btnRecord.getText().toString().contentEquals("resume")) {
                    mPublisher.resumeRecord();
                    btnRecord.setText("pause");
                }
            }
        });

        btnSwitchEncoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (btnSwitchEncoder.getText().toString().contentEquals("soft encoder")) {
                    mPublisher.switchToSoftEncoder();
                    btnSwitchEncoder.setText("hard encoder");
                } else if (btnSwitchEncoder.getText().toString().contentEquals("hard encoder")) {
                    mPublisher.switchToHardEncoder();
                    btnSwitchEncoder.setText("soft encoder");
                }*/
            }
        });


    }

    public void retro_putnoti(){
        api_follow.getRetrofit(getApplicationContext()).send(userid, usernick + "님이 " + "방송을 시작했습니다.").enqueue(new Callback<obj_serverresponse>() {
            @Override
            public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {

                obj_serverresponse serverresobj = response.body();
                if (serverresobj.getStatus().equals("OK")) {
                    Toast.makeText(getApplicationContext(), serverresobj.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<obj_serverresponse> call, Throwable t) {
                Log.d("mytag", "[adt_recy_follow]post follow error");
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        final Button btn = (Button) findViewById(R.id.publish);
        btn.setEnabled(true);
        mPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        btnRecord.setText("record");
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (btnPublish.getText().toString().contentEquals("stop")) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    private static String getRandomAlphaString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String getRandomAlphaDigitString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            btnPublish.setText("publish");
            btnRecord.setText("record");
            btnSwitchEncoder.setEnabled(true);
        } catch (Exception e1) {
            //
        }
    }

    // Implementation of SrsRtmpListener.

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Log.d("mytag","[post streaminfo] userid: "+userid+"usernick: "+usernick+"rmtitle:"+rmtitle+"rmid: "+rmid+" category"+spinner_category.getSelectedItem()+", privacy : "+spinner_privacy.getSelectedItem());

        //Stream list
        roomapi.getRetrofit(getApplicationContext()).post(rmid,rmtitle,userid,usernick,spinner_category.getSelectedItem().toString(),spinner_privacy.getSelectedItem().toString()).enqueue(new Callback<obj_serverresponse>() {
            @Override
            public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {
                Log.d("mytag","[post streaminginfo]response body"+response.body().toString());
                Log.d("mytag","[post streaminginfo]response message"+response.message());
                obj_serverresponse res=response.body();
                if(res.getStatus().equals("OK")) {
                    Toast.makeText(getApplicationContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                    //after post roominfo initialize chat component
                    initchat();
                }else{
                    Toast.makeText(getApplicationContext(), res.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<obj_serverresponse> call, Throwable t) {
                Log.d("mytag","[post streaminginfo]response failure");
            }
        });

        retro_putnoti();

    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();

        //Stream list
        roomapi.getRetrofit(getApplicationContext()).update(rmid).enqueue(new Callback<obj_room>() {
            @Override
            public void onResponse(Call<obj_room> call, Response<obj_room> response) {
                Log.d("mytag","[update streaminginfo]response body"+response.body().toString());
                Log.d("mytag","[update streaminginfo]response message"+response.message());
            }

            @Override
            public void onFailure(Call<obj_room> call, Throwable t) {
                Log.d("mytag","[update streaminginfo]response failure");
            }
        });
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    private void initfilterwidgets(){
        recy_filter=(RecyclerView)findViewById(R.id.recy_thumbnail);
        LinearLayoutManager layman_filter=new LinearLayoutManager(this);
        layman_filter.setOrientation(LinearLayoutManager.HORIZONTAL);
        layman_filter.scrollToPosition(0);
        recy_filter.setLayoutManager(layman_filter);
        recy_filter.setHasFixedSize(true);
        List<obj_filter_thumb> list_filter=new ArrayList<obj_filter_thumb>();
        list_filter.add(new obj_filter_thumb(R.drawable.person,"cool",MagicFilterType.COOL));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"beauty",MagicFilterType.BEAUTY));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"evergreen",MagicFilterType.EVERGREEN));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"nostalgia",MagicFilterType.NOSTALGIA));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"romance",MagicFilterType.ROMANCE));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"sunrise",MagicFilterType.SUNRISE));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"sunset",MagicFilterType.SUNSET));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"tender",MagicFilterType.TENDER));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"warm",MagicFilterType.WARM));
        list_filter.add(new obj_filter_thumb(R.drawable.person,"original",MagicFilterType.NONE));

        adt_recy_filter adt_filter=new adt_recy_filter(list_filter,this,this);
        recy_filter.setAdapter(adt_filter);
        adt_filter.notifyDataSetChanged();

    }

    public void initchat(){

        //set_chat Socket-(소켓 생성 및 read thread 생성)
        client = new thr_nettycli(ipaddress, 8007, m_Handler);
        client.start();
        Log.d("mytag", "client 생성");


    }



    @Override
    public void onThumbnailClick(MagicFilterType type) {
        mPublisher.switchCameraFilter(type);
    }
/*
    public CameraSource createCameraSource() {

        Context context = getApplicationContext();

        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        cameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setRequestedFps(30.0f)
                .build();



        return cameraSource;

    }
*/

public void startfacetrack(){

    if(flagfacetrack==false) {
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mPublisher.startface(detector);
        flagfacetrack=true;
    }else{
        mPublisher.stopface();
    }
}
    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            Log.d("mytag","[broadcaster graphicfactory]update");
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    public void postvideo(String pp,String rmid){
        Log.d("mytag",TAG+"postvideo");
        File file=new File(pp);
        Log.d("mytag","[main_fr_setting]pp : "+file.getName());
        MultipartBody.Part filepart=MultipartBody.Part.createFormData("file",file.getName(), RequestBody.create(MediaType.parse("video/*"),file));
        RequestBody req_body_rmid = RequestBody.create(MediaType.parse("text/plain"), rmid);
        roomapi.getRetrofit(getApplicationContext()).uploadVideo(filepart,req_body_rmid).enqueue(new Callback<obj_serverresponse>() {
            @Override
            public void onResponse(Call<obj_serverresponse> call, Response<obj_serverresponse> response) {
                Log.d("mytag","[main_fr_setting] "+response.message());

                obj_serverresponse serverres=response.body();
                try {
                    JSONObject userobj = new JSONObject(serverres.getData());

                }catch (JSONException e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), serverres.getMessage(), Toast.LENGTH_SHORT).show();            }

            @Override
            public void onFailure(Call<obj_serverresponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "네트워크 오류, 다시 실행해주세요", Toast.LENGTH_SHORT).show();
                Log.d("mytag","[main_fr_setting] putwithimg failure");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkreceiver);
        client.closesocket();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(lay_chat_pub.getVisibility()==View.VISIBLE){
            lay_chat_pub.setVisibility(View.GONE);
            lay_btn_pub.setVisibility(View.VISIBLE);
        }
    }
}
