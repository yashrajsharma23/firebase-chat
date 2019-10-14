package com.example.chatapp;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.contact_manager.CtManager;
import com.example.chatapp.model.PrefManager;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity{//} implements  ValueEventListener {

    private ActionBar toolbar;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    boolean isFirstLaunch = true;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog pd;
    Context ctx;
    public static MainActivity mainInstance;
    HashMap<String, String> refrence_token_map = new HashMap<>();

    PrefManager prefManager;

    public static MainActivity getInstance() {
        if (mainInstance == null) {
            mainInstance = new MainActivity();
        }
        return mainInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Check the Contact permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        toolbar.setTitle(getResources().getString(R.string.app_name));
        loadFragment(new ContactsFragment());

        ctx= getApplicationContext();
        prefManager= new PrefManager(ctx);

        pd = new ProgressDialog(ctx);
        pd.setMessage("Loading...");

        System.out.println("Login token ::: "+ prefManager.getFCMToken());

        ///To store Current updated FCM token
        Firebase.setAndroidContext(ctx);
        Firebase reference = new Firebase("https://chat-app-ab12cd34.firebaseio.com/users");
        reference.child("auth_token").child(prefManager.getSipNumber()).setValue(prefManager.getFCMToken());

        /*Intent i = getIntent();
        try {
            if (i.getExtras() != null) {
                String status = i.getStringExtra("status");
                if (status != null) {
                    if (status.equals("1")) {
                        System.out.println("Comes from notifications ");
                        String title = i.getStringExtra("title");
                        String token = i.getStringExtra("token");

                        UserDetails.chatWith = title;
                        Intent i1= new Intent(ctx,Chat.class);
                        i1.putExtra("token",token);
                        System.out.println("Token::: Users screen: "+ token+" name:"+UserDetails.chatWith);
                        startActivity(i1);
                        //startActivity(new Intent(ctx, Chat.class));


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        fetchChatContact();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        try {

            ///If User gets chat notification then for redirection to chat screen purpose
            if (i.getExtras() != null) {
                String status = i.getStringExtra("status");
                if (status != null) {
                    if (status.equals("1")) {
                        System.out.println("Comes from notifications ");
                        String title = i.getStringExtra("title");
                        String token = i.getStringExtra("token");

                        UserDetails.chatWith = title;
                        Intent i1= new Intent(ctx,Chat.class);
                        i1.putExtra("token",token);
                        System.out.println("Token::: Users screen: "+ token+" name:"+UserDetails.chatWith);
                        startActivity(i1);
                        //startActivity(new Intent(ctx, Chat.class));


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        boolean isPermissionGranted = false;
        int read_contact = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS);
        int write_contact = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_CONTACTS);

        if ( write_contact == PackageManager.PERMISSION_GRANTED && read_contact == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            // return true;
        } else {
            isPermissionGranted = false;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS
            }, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        return isPermissionGranted;
    }


    ///Fetch All the contacts that are registered in Firebase
    public void fetchChatContact(){

        String url = "https://chat-app-ab12cd34.firebaseio.com/users.json";
        System.out.println("Fetching Users : "+url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
    }


    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                ///Not to fetch current login user
                if(!key.equals(UserDetails.username)) {
                    al.add(key);

                    ///It will check the fetched chat contact whether that number is saved or not
                    //It will save those Firebase user number that are saved to current user phonebook
                    CtManager.updateChatAppContact(ctx,key,true);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        pd.dismiss();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean permission_st = false;
        if (isFirstLaunch) {
            isFirstLaunch = false;
            return;
        }

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (permission.equals(Manifest.permission.READ_CONTACTS) || permission.equals(Manifest.permission.WRITE_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {

                    }
                }

            }

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Fragment fragment;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_contacts:

                    //It will show all the contacts fetched from phonebook
                    fragment = new ContactsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_chats:

                    //It will show all the saved contact which are ready to chat
                    fragment = new Users();
                    loadFragment(fragment);
                    return true;

            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}