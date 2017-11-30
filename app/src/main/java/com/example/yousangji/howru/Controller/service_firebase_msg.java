package com.example.yousangji.howru.Controller;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.yousangji.howru.R;
import com.example.yousangji.howru.View.main_container;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by YouSangJi on 2017-10-31.
 */

public class service_firebase_msg extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    Messenger sMessengerToA;
    private int FCMMSG=0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Log.d("mytag","noti received");
        sendNotification(remoteMessage.getData().get("message"));
        Log.d("mytag","[servicefirebase]remotemessage");
        //sendMessageToUI(remoteMessage.getData());
        Intent intent=new Intent("updateui");
        intent.putExtra("message",remoteMessage.getData().get("message"));
        intent.putExtra("profileurl",remoteMessage.getData().get("profileurl"));
        intent.putExtra("userid",remoteMessage.getData().get("userid"));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //TODO: 메시지 핸들러로 보내기
        //TODO: 그전에 background foreground 확인하기.
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, main_container.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.livelogo)
                .setContentTitle("HOWRU")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendMessageToUI(Map<String,String> msgtosend) {

        try {

            //Send data as a String
            Bundle b = new Bundle();
            b.putString("msg",msgtosend.get("message"));
            b.putString("profileurl",msgtosend.get("profileurl"));
            Message msg = Message.obtain(null, FCMMSG);
            msg.setData(b);
            sMessengerToA.send(msg);

        }
        catch (RemoteException e) {
            sMessengerToA=null;
            Log.d("mytag","sendmessagetoUI error==>"+e.toString());
            e.printStackTrace();
        }

    }
}