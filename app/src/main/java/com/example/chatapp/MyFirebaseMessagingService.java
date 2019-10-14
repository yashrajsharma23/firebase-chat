package com.example.chatapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.chatapp.model.GlobalClass;
import com.example.chatapp.model.PrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    boolean checkapp;
    Context ctx;
    PrefManager pref;
    GlobalClass gc;
    String aToken;
    String message, status, title;
    Intent notificationIntent;
    int NOTIF_ID = 0;
    String CHANNEL_ID = "chatApp_channel_id1";
    public boolean xmppConnectionServiceBound = false;
    String deletedNumber ="",sourceNumber = "";
    private static final int NOTIFICATION_ID_MULTIPLIER = 1024 * 1024;

    public static final int NOTIFICATION_ID = 2 * NOTIFICATION_ID_MULTIPLIER;

    String TAG = "MyFirebaseMessagingService";

    public MyFirebaseMessagingService() {
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



    //Hitesh code
    @Override
    public void onCreate() {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
        Log.d(TAG, "------------*****onCreate*****--------: Called");
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "------------*****onRebind*****--------: Called");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        pref = new PrefManager(this);
        gc = GlobalClass.getInstance();
        ctx = this;

        String Data = remoteMessage.getData().toString();
        //String notification = remoteMessage.getNotification().toString();
        Log.d(TAG,"Notification Data "+Data);
        Log.d(TAG,"Notification message  "+remoteMessage.toString());
        System.out.println("Notification Data:::: "+ Data +" msg:"+remoteMessage.toString());

        if (!Data.equals("")) {

            status = remoteMessage.getData().get("status");
            title = remoteMessage.getData().get("title");

            if(status != null) {
                    message = remoteMessage.getData().get("message");

                sendNotification(title, message, status, remoteMessage);
            }

        } else {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String noti_title, String noti_message, String noti_status, RemoteMessage remoteMessage) {//,String noti_token) {


        Notification notif = null;
        String title = noti_title;
        String message = noti_message;
        String status_1 = noti_status;

        String isLogin = pref.isLoggedIn();
       // String[] data= status.split("1_");

       String status= status_1.substring(0,1);
        String token = status_1.substring(2,status_1.length());
        System.out.println("Notification Data::: "+ status+" Token:"+token);

        System.out.println("IsLogged In : "+isLogin);
        //Received Message notification
        if (status.equals("1")) {

            if(isLogin.equalsIgnoreCase("true")){

                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra("title", title);
                    notificationIntent.putExtra("message", message);
                    notificationIntent.putExtra("status", status);
                    notificationIntent.putExtra("token", token);
            }


            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIF_ID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


            //Creating notificaiton channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                /* Create or update. */
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, this.getResources().getString(R.string.general_other_notification),
                        NotificationManager.IMPORTANCE_DEFAULT);

                mChannel.enableLights(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(mChannel);
            }

            //Displaying notification on the basisi of versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    //If : Message is Image message
                    //Else : Text Message
                    if(message.contains("https://firebasestorage.")) {
                        Bitmap image=null;
                        try {
                            URL url = new URL(message);
                             image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch(IOException e) {
                            System.out.println(e);
                        }

                        notif = new Notification.Builder(this)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(title)
                                .setSmallIcon(getNotificationIcon())
                                .setSound(uri)
                                .setChannelId(CHANNEL_ID)
                                .setColor(getResources().getColor(R.color.tabtextselected))

                                //To show Image notification
                                .setStyle(new Notification.BigPictureStyle().bigPicture(image))
                                .build();
                        notif.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(NOTIF_ID, notif);
                    }else {
                        notif = new Notification.Builder(this)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(title)
                                .setSmallIcon(getNotificationIcon())
                                .setSound(uri)
                                .setChannelId(CHANNEL_ID)
                                .setColor(getResources().getColor(R.color.tabtextselected))
                                .setStyle(new Notification.BigTextStyle().bigText(message))
                                .build();
                        notif.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(NOTIF_ID, notif);
                    }
                    }

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    //If : Message is Image message
                    //Else : Text Message
                    if(message.contains("https://firebasestorage.")) {
                        Bitmap image=null;
                        try {
                            URL url = new URL(message);
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch(IOException e) {
                            System.out.println(e);
                        }

                        notif = new Notification.Builder(this)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(title)
                                .setSmallIcon(getNotificationIcon())
                                .setSound(uri)
                                .setColor(getResources().getColor(R.color.tabtextselected))

                                //To show Image notification
                                .setStyle(new Notification.BigPictureStyle().bigPicture(image))
                                .build();
                        notif.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(NOTIF_ID, notif);
                    }else {
                        notif = new Notification.Builder(this)
                                .setContentIntent(pendingIntent)
                                .setContentTitle(title)
                                .setSmallIcon(getNotificationIcon())
                                .setSound(uri)
                                .setColor(getResources().getColor(R.color.tabtextselected))
                                .setStyle(new Notification.BigTextStyle().bigText(message))
                                .build();
                        notif.flags |= Notification.FLAG_AUTO_CANCEL;
                        notificationManager.notify(NOTIF_ID, notif);
                    }


                } else {
                    notif = new Notification.Builder(this)
                            .setContentIntent(pendingIntent)
                            .setContentTitle(title)
                            .setSmallIcon(getNotificationIcon())
                            .setSound(uri)
                            .setColor(getResources().getColor(R.color.tabtextselected))
                            .setStyle(new Notification.BigTextStyle().bigText(message))
                            .build();
                    notif.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(NOTIF_ID, notif);
                    //disconnect(true);
                }
            }

    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

}
