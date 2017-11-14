package com.example.yousangji.howru.View;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yousangji.howru.Controller.adt_recy_chat;
import com.example.yousangji.howru.Controller.adt_recy_filter;
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
import com.google.gson.Gson;
import com.seu.magicfilter.utils.MagicFilterType;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class broadcaster extends AppCompatActivity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener,callback_filter {

    private static final String TAG = "Yasea";

    private Button btnPublish;
    private ImageView btnSwitchCamera;
    private ImageView btnRecord;
    private ImageView btnSwitchEncoder;
    private ImageButton btn_chat_show;
    private LinearLayout lay_chat_pub;



    private SharedPreferences sp;
    private String rtmpUrl = "rtmp://ossrs.net/" + getRandomAlphaString(3) + '/' + getRandomAlphaDigitString(5);
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";

    private SrsPublisher mPublisher;

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

    private Handler m_Handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0: // 소켓 생성 완료
                    // 토스트
                    Toast.makeText(broadcaster.this, "socket setting", Toast.LENGTH_SHORT).show();
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
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btnRecord = (ImageView) findViewById(R.id.record);
        btnSwitchEncoder = (ImageView) findViewById(R.id.swEnc);
        btn_filter=(ImageButton)findViewById(R.id.btn_filter);
        btn_chat_show=(ImageButton)findViewById(R.id.btn_chat_pub);

        //set_chat component
        chatmst_list=(RecyclerView) findViewById(R.id.recy_chat_pub);
        btnchat_favorite=(ImageView)findViewById(R.id.btnchat_show);
        chat_edit=(EditText)findViewById(R.id.editchat_text);
        btnchat_sbm=(ImageView) findViewById(R.id.btn_chat_sbm_pub);
        lay_chat_pub=(LinearLayout)findViewById(R.id.lay_chat_pub);


        //set_chat recyclerview
        chatmst_list.setHasFixedSize(true);
        chat_layman=new LinearLayoutManager(this);
        chatmst_list.setLayoutManager(chat_layman);
        chat_recyadapter=new adt_recy_chat(this);
        chatmst_list.setAdapter(chat_recyadapter);


        btn_chat_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lay_chat_pub.getVisibility()==View.VISIBLE) {
                    lay_chat_pub.setVisibility(View.GONE);
                }else{
                    lay_chat_pub.setVisibility(View.VISIBLE);
                }
            }
        });


        btnchat_sbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_msgcontent=chat_edit.getText().toString();
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


        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
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
                    usernick=userid;
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
                    //btnRecord.setText("record");
                    btnSwitchEncoder.setEnabled(true);
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
               /*
                if (btnRecord.getImageAlpha()==80) {
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
                btnRecord.setImageAlpha(0);*/
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


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else {
            switch (id) {
                case R.id.cool_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.COOL);
                    break;
                case R.id.beauty_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.BEAUTY);
                    break;
                case R.id.early_bird_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EARLYBIRD);
                    break;
                case R.id.evergreen_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EVERGREEN);
                    break;
                case R.id.n1977_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.N1977);
                    break;
                case R.id.nostalgia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.NOSTALGIA);
                    break;
                case R.id.romance_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.ROMANCE);
                    break;
                case R.id.sunrise_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNRISE);
                    break;
                case R.id.sunset_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNSET);
                    break;
                case R.id.tender_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TENDER);
                    break;
                case R.id.toast_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TOASTER2);
                    break;
                case R.id.valencia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.VALENCIA);
                    break;
                case R.id.walden_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WALDEN);
                    break;
                case R.id.warm_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WARM);
                    break;
                case R.id.original_filter:
                default:
                    mPublisher.switchCameraFilter(MagicFilterType.NONE);
                    break;
            }
        }
        setTitle(item.getTitle());

        return super.onOptionsItemSelected(item);
    }*/

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
        //btnRecord.setText("record");
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
           // btnRecord.setText("record");
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
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        Log.d("mytag","[post streaminfo] userid: "+userid+"usernick: "+usernick+"rmtitle:"+rmtitle+"rmid: "+rmid);

        //Stream list
        roomapi.getRetrofit(getApplicationContext()).post(rmid,rmtitle,userid,usernick).enqueue(new Callback<obj_room>() {
            @Override
            public void onResponse(Call<obj_room> call, Response<obj_room> response) {
                Log.d("mytag","[post streaminginfo]response body"+response.body().toString());
                Log.d("mytag","[post streaminginfo]response message"+response.message());

                //after post roominfo initialize chat component
                initchat();
            }

            @Override
            public void onFailure(Call<obj_room> call, Throwable t) {
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

        adt_recy_filter adt_filter=new adt_recy_filter(list_filter,this);
        recy_filter.setAdapter(adt_filter);
        adt_filter.notifyDataSetChanged();

    }

    public void initchat(){

        //set_chat Socket-(소켓 생성 및 read thread 생성)
        client = new thr_nettycli(ipaddress, 8007, m_Handler);
        client.start();
        Log.d("mytag", "client 생성");


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
    }

    @Override
    public void onThumbnailClick(MagicFilterType type) {
        mPublisher.switchCameraFilter(type);
    }
}
