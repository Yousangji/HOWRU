package com.example.yousangji.howru.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by YouSangJi on 2017-11-22.
 */

public class Network extends BroadcastReceiver {

    private Handler m_handler;

    public Network() {
        super();
    }

    public Network(Handler handler) {
        m_handler=handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

            if (isInitialStickyBroadcast()) {
                //do nothing if called on create
            } else {
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    try {
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
                        NetworkInfo _wifi_network =
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        Log.d("mytag", "[network]" + activeNetInfo.getDetailedState());
                        Log.d("mytag", "[network]" + _wifi_network.getDetailedState());
                        if (_wifi_network != null) {
                            Log.d("mytag", "wifi network is not null");
                            // wifi, 3g 둘 중 하나라도 있을 경우
                            if (_wifi_network != null && activeNetInfo != null) {
                                //TODO; handler send message to connect again
                                // 메시지 얻어오기
                                Message handlermsg = m_handler.obtainMessage();
                                // 메시지 ID 설정
                                handlermsg.what = 2;
                                // 메시지 정보 설정3 (Object 형식)
                                m_handler.sendMessage(handlermsg);
                            }
                            // wifi, 3g 둘 다 없을 경우
                            else {
                                //TODO: send message by handler to close connect
                                // 메시지 얻어오기
                                Message handlermsg = m_handler.obtainMessage();
                                // 메시지 ID 설정
                                handlermsg.what = 3;
                                // 메시지 정보 설정3 (Object 형식)
                                m_handler.sendMessage(handlermsg);
                            }
                        }
                    } catch (Exception e) {
                        Log.i("ULNetworkReceiver", e.getMessage());
                    }
                }
            }
        }
    }

