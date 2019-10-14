package com.contact_manager;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Contact_sync extends Service {

    private static final String TAG = Contact_sync.class.getSimpleName();

    public Context ctx;
    public String dynamic_table;
    // public String fdynamic_table;
    public Editor edit;
    public String gen = "";
    public String contact = "";
    // public String fgen = "";
    public String fcontact = "";
    public int totalcontact = 0;
    public String test = "";
    public String contactNo = "";
    public String strcode = "0";
    public String intcode = "0";
    public int code_length = 0;
    public boolean isValid = false;
    InputStream is = null;
    public int backgroung_flag = 0;
    public String sipdata = "";
    public Thread contact_notifyingThread;
    public Boolean contact_bool_check = true;
    private ConditionVariable contact_mCondition;
    public int flag = 0;
    public String server_ip = "";
    public int favflag = 0;
    String readadress = "";
    String username;
    private static final String WIZARD_PREF_NAME = "Register Preference";
    String api_ip = "";
    SharedPreferences sh_pref_cal;
    String sip_contacts;
    String con;
    private static final int NOTIFICATION_ID_MULTIPLIER = 1024 * 1024;
    public static final int FOREGROUND_NOTIFICATION_ID = NOTIFICATION_ID_MULTIPLIER * 4;

    public Contact_sync() {
    }

    public Contact_sync(Context ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log.v("Contact_sync", "onstart");
        // toCallAsynchronous();
        System.out.println("Contact_Sync: onStartCommand");

        //startTimer();


      /*  test = PreferenceManager.getDefaultSharedPreferences(ctx).getString(ContactsContract.Settings.PREF_SIPARRAY + "", ContactsContract.Settings.DEFAULT_SIPARRAY);
        // Log.v("test", test);
        server_ip = PreferenceManager.getDefaultSharedPreferences(ctx).getString(ContactsContract.Settings.PREFSIP_SERVER + "", ContactsContract.Settings.DEFAULTSIP_SERVER);
        registerForContactsResolver();

        // username = PreferenceManager.getDefaultSharedPreferences(ctx)
        // .getString(Settings.PREFMCRM_MSIDN + "",
        // Settings.DEFAULTMCRM_MSIDfN);
        username = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Settings.PREFSIP_USERNAME + "", Settings.DEFAULTSIP_USERNAME);

        contact_notifyingThread = new Thread(null, contact_mTask, "Contact_sync_Service");
        contact_mCondition = new ConditionVariable(false);
        contact_notifyingThread.start();*/


        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        getpreference();
        Log.v("Contact_sync", "oncreate");
        ctx = this;

        System.out.println("Contact_Sync: onCreate");
        if (checkStoragePermission()){
            registerForContactsResolver();
        }
        /*final Notification notification = createForegroundNotification();
        startForeground(FOREGROUND_NOTIFICATION_ID, notification);*/

        }
    private boolean checkStoragePermission() {
        int read_contact = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
        int write_contact = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CONTACTS);
        return read_contact == PackageManager.PERMISSION_GRANTED
                && write_contact == PackageManager.PERMISSION_GRANTED;
    }
    // for register content observer
    ContactsChangeObserver contactObserver2;

    public void registerForContactsResolver() {
        contactObserver2 = new ContactsChangeObserver(new Handler(), getApplicationContext());
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, contactObserver2);
    }

    private Runnable contact_mTask = new Runnable() {
        public void run() {
            // contact_bool_check=true;
            while (contact_bool_check) {
                Log.v("contact_sync flag", "" + flag);
                if (flag == 0) {
                } else {
                    contact_mCondition.block(2 * 1000 * 60);// 15 minute

                }
                if (contact_bool_check) {
                    flag = 1;
                }
                if (!contact_bool_check) {
                    if (contact_notifyingThread != null) {
                        contact_notifyingThread.interrupt();
                        contact_notifyingThread = null;
                    }
                }

                // contact_mCondition.block(900000);// 15 minutes
            }
        }
    };

    public void stopThread() {

        if (contact_notifyingThread != null) {
            if (contact_notifyingThread.isAlive()) {
                contact_notifyingThread.interrupt();
            }
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println("Contact_Sync: onTaskRemoved");

       /* Intent broadcastIntent = new Intent(SipManager.INTENT_BACKGROUND_CONTACT_SERVICE_RESTART);
        sendBroadcast(broadcastIntent);*/
        // Your job when the service stops.
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("Contact_Sync: onUnbind");

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        contact_bool_check = false;
        /*stopSelf();
        stopThread();*/
        Log.v("contact_sync", "ondestroyee");
        System.out.println("Contact_Sync: OnDestroy");

        /*Intent broadcastIntent = new Intent(SipManager.INTENT_BACKGROUND_CONTACT_SERVICE_RESTART);
        sendBroadcast(broadcastIntent);*/
        //this.getApplicationContext().getContentResolver().registerContentObserver(new ContactsChangeObserver(new Handler(), this));
        super.onDestroy();
    }

    public int counter=0;
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

   /* @RequiresApi(api = Build.VERSION_CODES.O)
    Notification createForegroundNotification() {
        String channel_ID="";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel_ID=   createNotificationChannel("Foreground", "My Background Service");
        }


        final Notification.Builder mBuilder = new Notification.Builder(Contact_sync.this);
        mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        int enabled = 0;
        int connected = 0;
        mBuilder.setContentIntent(createOpenConversationsIntent());
        mBuilder.setWhen(0);
        mBuilder.setPriority(Notification.PRIORITY_MIN);*//*
//        mBuilder.setSmallIcon(connected > 0 ? R.mipmap.inextrix_launcher : R.mipmap.inextrix_launcher);

       *//* if (Compatibility.runsTwentySix()) {
            mBuilder.setChannelId(channel_ID);
        }*//*


        return mBuilder.build();
    }
*/

   /* @RequiresApi(Build.VERSION_CODES.O)
    private String  createNotificationChannel(String channelId,String channelName) {

       *//* NotificationChannel chan =new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);*//*


        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        service.createNotificationChannelGroup(new NotificationChannelGroup("status", ctx.getString(R.string.notification_group_status_information)));

        *//*NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setPriority(Notification.PRIORITY_MIN);
        builder.setChannelId(channelId);
        builder.setSmallIcon(R.mipmap.inextrix_launcher);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        service.notify(1, notification);*//*


//        NotificationChannel mChannel = new NotificationChannel(channelId, ctx.getResources().getString(R.string.foreground_service_channel_name),
                NotificationManager.IMPORTANCE_MIN);
//        mChannel.setDescription(ctx.getString(R.string.foreground_service_channel_description));
//        mChannel.setShowBadge(false);
//        mChannel.setGroup("status");
//        mChannel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC);
//
//        service.createNotificationChannel(mChannel);
        return channelId;
    }*/


    /*private PendingIntent createOpenConversationsIntent() {

        return PendingIntent.getActivity(Contact_sync.this, 0, new Intent(Contact_sync.this, MainActivity.class), 0);
    }*/

    public int gen() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

    public int fgen() {
        Random r = new Random(System.currentTimeMillis());
        return 30000 + r.nextInt(40000);
    }

    public String deDup(String s) {
        // TODO Auto-generated method stub
        return new LinkedHashSet<String>(Arrays.asList(s.split(","))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void getpreference() {
        // TODO Auto-generated method stub
        sh_pref_cal = getSharedPreferences(WIZARD_PREF_NAME, Context.MODE_PRIVATE);
        api_ip = sh_pref_cal.getString("APIIP", "");
        System.out.println(api_ip);
    }



}