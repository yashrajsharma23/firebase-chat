package com.example.chatapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


//@Obfuscate
public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "com.example.chatapp_Preferences";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public static final String KEY_CONTACT_SYNC="KEY_CONTACT_SYNC";
    private static final String KEY_SIP_NUMBER = "sip_number";
    private static final String KEY_DEVICEID = "device_id";
    private static final String KEY_FAV_FLAG= "fav_flag";
    public static final String KEY_FCM_TOKEN = "fcm_token";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsContactSync(boolean isContactSync){
        editor.putBoolean(KEY_CONTACT_SYNC,isContactSync);
        editor.commit();
    }

    public boolean getIsContactSync(){
        return  pref.getBoolean(KEY_CONTACT_SYNC,false);
    }

    public void setLoggedIn(String setLogin) {
        editor.putString(KEY_IS_LOGGED_IN, setLogin);
        editor.commit();
    }
    public String isLoggedIn(){
        return pref.getString(KEY_IS_LOGGED_IN, "no");
    }



    public String getSipNumber() {
        return pref.getString(KEY_SIP_NUMBER, "");
    }

    public void setSipNumber(String number) {
        editor.putString(KEY_SIP_NUMBER, number);
        editor.commit();
    }

    public void setKEY_DeviceId(String deviceid) {
        editor.putString(KEY_DEVICEID, deviceid);
        editor.commit();
    }

    public String getKEY_DeviceId() {
        return pref.getString(KEY_DEVICEID, null);
    }

    public String getFavFlagUpdated() {
        return pref.getString(KEY_FAV_FLAG, "no");
    }

    public void setFavFlagUpdated(String FavFlagUpdated) {
        editor.putString(KEY_FAV_FLAG, FavFlagUpdated);
        editor.commit();
    }

    public void setFCMToken(String fcm_token){
        editor.putString(KEY_FCM_TOKEN,fcm_token);
        editor.commit();
    }

    public String getFCMToken(){
        return pref.getString(KEY_FCM_TOKEN,null);
    }

}

