package com.example.chatapp;


import android.util.Log;

import com.example.chatapp.model.PrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by inextrix on 28/3/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    PrefManager pref;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFirebaseInstance", "Instance token: " + refreshedToken);

        pref = new PrefManager(this);
        pref.setFCMToken(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

}
