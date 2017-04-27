package com.virtualdusk.zezgn.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import com.virtualdusk.zezgn.MainActivity;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Home;

/**
 * Created by bhart.gupta on 30-Nov-16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String  strTitle,strBody,strType,strBadge;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional


        //Calling method to generate notification
        Log.d(TAG, "onMessageReceived: background " + Utilities.isAppIsInBackground(this));

        if (remoteMessage == null)
            return;
        if (remoteMessage.getNotification() != null) {

            strBody = remoteMessage.getNotification().getBody() + "";
            strTitle = remoteMessage.getNotification().getBody() + "";
            String strSound = remoteMessage.getNotification().getSound() + "";
            Log.d(TAG, "Notification strBody: " + strBody);
            Log.d(TAG, "Notification strTitle: " + strTitle);
            Log.d(TAG, "Notification strSound: " + strSound);

            if (remoteMessage.getData() != null) {

                strBadge = remoteMessage.getData().get("badge")+"";
                strType = remoteMessage.getData().get("type")+"";
                Log.d(TAG, "Notification Type: " + strType);
                Log.d(TAG, "Notification Badge: " + strBadge);
            }

            if (!Utilities.isAppIsInBackground(getApplicationContext())) {
                sendNotification(strTitle,strBody, strType);
            }
        }


    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageTItle, String messageBody, String messageType) {

        Intent intent = new Intent(this, Home.class);
        intent.putExtra(Constant.NOTIFICATION_BODY, messageBody);
        intent.putExtra(Constant.NOTIFICATION_TITLE, messageTItle);
        intent.putExtra(Constant.NOTIFICATION_TYPE, messageType);
        intent.putExtra(Constant.IS_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //   Bitmap large_icon = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(messageTItle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}