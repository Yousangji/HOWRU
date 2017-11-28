package com.example.yousangji.howru.View;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yousangji.howru.Controller.Network;
import com.example.yousangji.howru.Controller.adt_recy_chat;
import com.example.yousangji.howru.Model.api_url;
import com.example.yousangji.howru.Model.obj_chatmsg;
import com.example.yousangji.howru.Model.obj_room;
import com.example.yousangji.howru.Model.thr_nettycli;
import com.example.yousangji.howru.R;
import com.example.yousangji.howru.Util.util_sharedpref;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by YouSangJi on 2017-10-24.
 */

public class viewer extends AppCompatActivity implements VideoRendererEventListener {

    private static final String TAG = "viewer";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    private TextView resolutionTextView;

    //chat
    Gson gson;
    private RecyclerView chatmst_list;
    private RecyclerView.LayoutManager chat_layman;
    private adt_recy_chat chat_recyadapter;
    private ImageView btnchat_show;
    private EditText chat_edit;
    private ImageView btnchat_sbm;

    //private clientSocketchannel socketchannel_obj;
    private thr_nettycli client;
    private String m_msgcontent=null;
    private String str_msgobj_send=null;

    //chatServer_info
    private String ipaddress="52.78.169.32";
    private int Port=7777;
    private ProgressBar m_pdIsLoading;
    private boolean iswatching=false;

    obj_chatmsg msgobj;
    obj_chatmsg msgobj_received;

    //test
    private boolean flag=true;
    //userinfo
    private String userid;
    private String url_profile;
    private String nickname;
    private String rmnum;
    private String roomid;
    private String rminfo;
    private String url;
    private obj_room rmobj;
    private util_sharedpref prefutil;

    Network networkreceiver;



    private Handler m_Handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0: // 소켓 생성 완료
                    // 소켓 연결 토스트
                    Toast.makeText(viewer.this, "socket setting", Toast.LENGTH_SHORT).show();

                    if(!iswatching) {
                        msgobj = new obj_chatmsg();
                        msgobj.setMsg_state("0");
                        msgobj.setMsg_nickname(nickname);
                        msgobj.setMsg_userid(userid);
                        msgobj.setMsg_profileurl(url_profile);
                        msgobj.setMsg_rmnum(roomid);
                        msgobj.setMsg_content(nickname + "님이 입장하셨습니다.");


                        str_msgobj_send = gson.toJson(msgobj);
                        client.sendMsg(str_msgobj_send);
                        Log.d("mytag", "[viewer] sendsettingmsg" + str_msgobj_send);
                        chat_recyadapter.addmsg(msgobj);
                        chat_recyadapter.notifyDataSetChanged();
                        iswatching=true;
                    }else{
                        Toast.makeText(viewer.this, "재연결 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 1: // 데이터 수신 완료
                    // 수신 데이터 토스트로 띄움.
                    Toast.makeText(viewer.this, "server responded : " + msg.obj, Toast.LENGTH_SHORT).show();

                        //Json으로 받은 데이터를 list에 추가
                        Gson gson_recv=new Gson();
                        msgobj_received=gson_recv.fromJson(msg.obj.toString(),obj_chatmsg.class);
                        chat_recyadapter.addmsg(msgobj_received);
                        chat_recyadapter.notifyDataSetChanged();
                    Log.d("mytag","[viewer]received msg : "+msg.obj.toString());
                    break;

                case 2:
                    //set_chat Socket-(소켓 생성 및 read thread 생성)
                    client = new thr_nettycli(ipaddress, 8007, m_Handler);
                    client.start();
                    Log.d("mytag", "client 재생성");
                    Toast.makeText(viewer.this, "방송에 재연결합니다.", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(viewer.this, "네트워크 연결이 종료 되었습니다. 네트워크 연결시 방송에 재연결합니다.", Toast.LENGTH_SHORT).show();
                    break;

                case -1:
                    //TODO: message return from server OK
                    Log.d("mytag","[viewer]message return from server OK");
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_viewer);




        Intent getroomid=getIntent();
        rminfo=getroomid.getStringExtra("rminfo");
        rmobj=new obj_room();
         gson=new GsonBuilder().create();
        rmobj=gson.fromJson(rminfo,obj_room.class);
        url=rmobj.getRmpath();
        roomid=rmobj.getRoomid();

        //shared
        util_sharedpref.createInstance(getApplicationContext());
        prefutil=util_sharedpref.getInstance();
        userid=prefutil.getString("userid");
        nickname=prefutil.getString("nickname");
        url_profile=prefutil.getString("profileurl");
        Log.d("mytag","[list_follow] userid : "+userid);


        resolutionTextView = new TextView(this);
        resolutionTextView = (TextView) findViewById(R.id.resolution_textView);

        //set_chat component
        chatmst_list=(RecyclerView) findViewById(R.id.recychatmsg);
        btnchat_show=(ImageView)findViewById(R.id.btnchat_show);
        chat_edit=(EditText)findViewById(R.id.editchat_text);
        btnchat_sbm=(ImageView) findViewById(R.id.btnchat_sbm);


        //set_chat recyclerview
        chatmst_list.setHasFixedSize(true);
        chat_layman=new LinearLayoutManager(this);
        chatmst_list.setLayoutManager(chat_layman);
        chat_recyadapter=new adt_recy_chat(this);
        chatmst_list.setAdapter(chat_recyadapter);

        //set_chat Socket-(소켓 생성 및 read thread 생성)
        //socketchannel_obj=new clientSocketchannel(ipaddress,Port,m_Handler);
        client = new thr_nettycli(ipaddress, 8007, m_Handler);
        client.start();
        Log.d("mytag", "client 생성");



        rmnum=rmobj.getRoomid();

        btnchat_sbm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_msgcontent=chat_edit.getText().toString();
                ////////////////temp : 방번호 nickname 입력 => 접속
                msgobj=new obj_chatmsg();
                msgobj.setMsg_state("3");
                msgobj.setMsg_nickname(nickname);
                msgobj.setMsg_userid(userid);
                msgobj.setMsg_profileurl(url_profile);
                msgobj.setMsg_rmnum(roomid);
                msgobj.setMsg_content(m_msgcontent);
                chat_recyadapter.addmsg(msgobj);
                chat_recyadapter.notifyDataSetChanged();
                str_msgobj_send=gson.toJson(msgobj);
                client.sendMsg(str_msgobj_send);
                Log.d("mytag","[viewer] sendmsg" +str_msgobj_send);
                chat_edit.setText("");
            }
        });


// 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

//Set media controller
        // simpleExoPlayerView.setUseController(true);
        // simpleExoPlayerView.requestFocus();

// Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);


// I. ADJUST HERE:
//CHOOSE CONTENT: LiveStream / SdCard

//LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:

//        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
        Uri mp4VideoUri =Uri.parse(api_url.API_BASE_URL+url); //Radnom 540p indian channel
        Log.d("mytag","[stream url]"+api_url.API_BASE_URL+url);
//        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------


//VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
     //   String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
//        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);





//Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
//Produces DataSource instances through which media data is loaded.

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
//Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


// II. ADJUST HERE:

//This is the MediaSource representing the media to be played:


        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);

        // videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

//FOR SD CARD SOURCE:


//FOR LIVESTREAM LINK:

        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

// Prepare the player with the source.
        player.prepare(loopingSource);

        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.v(TAG, "Listener-onTimelineChanged...");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged...");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v(TAG, "Listener-onLoadingChanged...isLoading:"+isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v(TAG, "Listener-onRepeatModeChanged...");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.v(TAG, "Listener-onPositionDiscontinuity...");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v(TAG, "Listener-onPlaybackParametersChanged...");
            }
        });

        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution

        //네트워크 Check 등록
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkreceiver=new Network(m_Handler);
        registerReceiver(networkreceiver,filter);

    }//End of onCreate

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(com.google.android.exoplayer2.Format format) {

    }


    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged ["  + " width: " + width + " height: " + height + "]");
        resolutionTextView.setText("RES:(WxH):"+width+"X"+height +"\n           "+height+"p");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }




//-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkreceiver);
        client.closesocket();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }
}
