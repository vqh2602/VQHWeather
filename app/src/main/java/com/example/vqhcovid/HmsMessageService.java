package com.example.vqhcovid;

import android.content.Intent;
import android.util.Log;

import com.huawei.hms.hwid.I;
import com.huawei.hms.push.RemoteMessage;

import java.util.Map;

public class HmsMessageService extends com.huawei.hms.push.HmsMessageService {

    private static final String TAG = "HMSPush";


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "receive:" + token);
        sendTokenToDisplay(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.i(TAG, "onMessageReceived is called");

        // Check whether the message is empty.
        if (message == null) {
            Log.e(TAG, "Received message entity is null!");
            return;
        }

//        Log.i(TAG, "get Data: " + message.getData()
//                + "\n getFrom: " + message.getFrom()
//                + "\n getTo: " + message.getTo()
//                + "\n getMessageId: " + message.getMessageId()
//                + "\n getSendTime: " + message.getSentTime()
//                + "\n getDataMap: " + message.getDataOfMap()
//                + "\n getMessageType: " + message.getMessageType()
//                + "\n getTtl: " + message.getTtl()
//                + "\n getToken: " + message.getToken());

//        Boolean judgeWhetherIn10s = false;
//        // If the message is not processed within 10 seconds, create a job to process it.
//        if (judgeWhetherIn10s) {
//            startWorkManagerJob(message);
//        } else {
//            // Process the message within 10 seconds.
//            processWithin10s(message);
//        }
        Map<String, String> data = message.getDataOfMap();
        sendDataMsgtoDisplay(data.get("title"), data.get("content"));
    }


//    private void startWorkManagerJob(RemoteMessage message) {
//        Log.d(TAG, "Start new job processing.");
//    }
//    private void processWithin10s(RemoteMessage message) {
//        Log.d(TAG, "Processing now.");
//    }


    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    private void sendTokenToDisplay(String token){
        Intent intent = new Intent("com.example.vqhcovid.ON_NEW_TOKEN");
        intent.putExtra("Token", token);
        sendBroadcast(intent);
    }

    private void sendDataMsgtoDisplay(String title, String content){
        Intent intent = new Intent("com.example.vqhcovid.ON_MSG_RECEIVED");
        intent.putExtra("Title", title);
        intent.putExtra("Content", title);
        sendBroadcast(intent);
    }
}