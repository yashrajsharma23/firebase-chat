package com.contact_manager;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.example.chatapp.model.PrefManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;


public class ContactsChangeObserver extends ContentObserver {
    private static final String TAG = ContactsChangeObserver.class.getName();
    private final Object contactsSync = new Object();
    Context context;
    Editor edit;
    String dynamic_table;
    String contactNo = "";
    String gen = "";
    long lastTimeofCall = 0L;
    long lastTimeofUpdate = 0L;
    long threshold_time = 10000;
    private String isoCountry;
    //    private PhoneBookRecord[] currentPhoneBook;
    private boolean isSynced = false;
    private long lastContactsChanged = 0;
    private HashMap<String, String> map = new HashMap<>();
    PrefManager prefManager;

    public ContactsChangeObserver(Handler handler, Context context) {
        super(handler);
        //Fabric.with(context, new Crashlytics());
        this.context = context;
        map = new HashMap<>();
        prefManager = new PrefManager(context);
        System.out.println("Contact_Sync: ContactsChangeObserver : ContactsChangeObserver");

    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
        System.out.println("Contact_Sync: ContactsChangeObserver : onChange 1");

        //Toast.makeText(context, "onselfChange ", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        // do s.th.

        try {
            System.out.println("Contact_Sync: ContactsChangeObserver : onChange 2");
            //contactsPreSync();
            reloadPhoneBook();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Contact_Sync: ContactsChangeObserver : onChange: Exception " + e);

        }
        // depending on the handler you might be on the UI
        // thread, so be cautious!

        //Toast.makeText(context, "onChange", Toast.LENGTH_LONG).show();

        // to start contact sync pre-process

    }

    protected void contactsPreSync() throws Exception {

        Runnable contact_mTask = new Runnable() {
            public void run() {
                System.out.println("Contact_Sync: ContactsChangeObserver : contactsPreSync");

                reloadPhoneBook();
            }
        };

        Thread contact_notifyingThread = new Thread(null, contact_mTask, "Contact_sync_Service");
        contact_notifyingThread.start();

    }

    public void reloadPhoneBook() {
        try {
            if (prefManager.getIsContactSync()) {
                return;
            }


            /*if (ContactsFragment.getInstance() != null) {
                ContactsFragment.getInstance().showProgress();

            }

            prefManager.setIsContactSync(true);

            CtManager.fetchContact(context, contacts -> {

                if (ContactsFragment.getInstance() != null) {
                    ContactsFragment.getInstance().autoUpdateContact();
                }


                if (FavContacts.getInstance() != null) {
                    FavContacts.getInstance().autoUpdateContact();
                }

                prefManager.setIsContactSync(false);
                return null;
            });
*/
        } catch (Exception e) {
            prefManager.setIsContactSync(false);

        }

    }

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
}