package com.example.chatapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.contact_manager.CtManager;
import com.example.chatapp.model.PrefManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Login extends AppCompatActivity {

    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;

    Context ctx;
    String deviceId;
    PrefManager prefsManager;
    boolean isRequestPermisssion = false;
    public static final int PERMISSION_REQUEST_CONTACT = 86;
    boolean displayDialog = false;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getApplicationContext();
        prefsManager = new PrefManager(ctx);

       /* FirebaseApp.initializeApp(getApplicationContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);*/

        if (prefsManager.isLoggedIn().equals("true")) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);

            registerUser = (TextView) findViewById(R.id.register);
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                checkContactPermission();
                syncContact();

            } else {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.
                        TELEPHONY_SERVICE);
                deviceId = telephonyManager.getDeviceId();
                System.out.println("Device ID :" + deviceId);
                if (deviceId == null || deviceId.length() == 0) {
                    deviceId = UUID.randomUUID().toString();
                    prefsManager.setKEY_DeviceId(deviceId);
                } else {
                    prefsManager.setKEY_DeviceId(deviceId);
                }
            }


            loginButton = (Button) findViewById(R.id.loginButton);


            registerUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, Register.class));
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString();
                    pass = password.getText().toString();

                    if (user.equals("")) {
                        username.setError("can't be blank");
                    } else if (pass.equals("")) {
                        password.setError("can't be blank");
                    } else {
                        String url = "https://chat-app-ab12cd34.firebaseio.com/users.json";
                        final ProgressDialog pd = new ProgressDialog(Login.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("null")) {
                                    Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if (!obj.has(user)) {
                                            Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                                        } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                                            prefsManager.setLoggedIn("true");
                                            prefsManager.setSipNumber(user);

                                            UserDetails.username = user;
                                            UserDetails.password = pass;
                                            startActivity(new Intent(Login.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                pd.dismiss();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);
                                pd.dismiss();
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                        rQueue.add(request);
                    }

                }
            });
        }
    }


    public void syncContact() {

        if (checkPermission()) {
            try {
                if (CtManager.getListOfContacts(this) == null || CtManager.getListOfContacts(this).size() == 0) {
                    CtManager.fetchContact(this, contacts -> {
                        return null;
                    });
                }
            } catch (Exception e) {
                Log.d("Login:: ", "onCreate: error in contact Sync:: " + e);
            }

        }

    }


    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            isRequestPermisssion = true;

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE
                    , Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS}, PERMISSION_REQUEST_CONTACT);
            return false;
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.
                    TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            System.out.println("Device ID :" + deviceId);
            if (deviceId == null || deviceId.length() == 0) {
                deviceId = UUID.randomUUID().toString();
                prefsManager.setKEY_DeviceId(deviceId);
            } else {
                prefsManager.setKEY_DeviceId(deviceId);
            }
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CONTACT) {
            boolean isPermissionGranted = true;
            isRequestPermisssion = false;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(android.Manifest.permission.READ_PHONE_STATE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission Granted : " + permission);
                        // Permission is granted
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.
                                TELEPHONY_SERVICE);

                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        deviceId = telephonyManager.getDeviceId();
                        if (deviceId == null || deviceId.length() == 0) {
                            deviceId = UUID.randomUUID().toString();
                            prefsManager.setKEY_DeviceId(deviceId);
                        } else {
                            prefsManager.setKEY_DeviceId(deviceId);
                        }
                        isPermissionGranted = true;
                    } else {
                        System.out.println("Permission Granted failed : " + permission);
                        isPermissionGranted = false;
                    }
                } else if (permission.equals(android.Manifest.permission.READ_CONTACTS) || permission.equals(Manifest.permission.WRITE_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permission Granted : " + permission);
                    } else {
                        System.out.println("Permission Granted failed : " + permission);
                    }

                    isPermissionGranted = true;
                    /*syncContact();*/
                }
            }
            if (!isPermissionGranted && !displayDialog) {
                openPermissionDialog();
            }

        }
    }


    private void openPermissionDialog() {

        Log.d("LoginActivity: ", "openPermissionDialog: called");
        displayDialog = true;

        new AlertDialog.Builder(this)

                .setTitle(ctx.getResources().getString(R.string.permission_title))
                .setMessage(ctx.getResources().getString(R.string.per_phone_state_desc))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        displayDialog = false;
                        try {
                            //Open the specific App Info page:
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);

                        } catch (ActivityNotFoundException e) {
                            //Open the generic Apps page:

                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);

                        }
                    }
                })
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .show().setCanceledOnTouchOutside(false);

    }

}
