package com.example.chatapp;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatapp.model.GlobalClass;
import com.example.chatapp.model.PrefManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    ImageView attachment;
    public static final int ATTACHMENT_CHOICE_CHOOSE_IMAGE = 0x0301;

    EditText messageArea;
    ScrollView scrollView;
    ImageView imageView;
    Firebase reference1, reference2;
    private ActionBar toolbar;
    Toolbar tool;
    TextView tv;
    String token;
    String my_token;

    PrefManager prefManager;
    LinearLayout msg_ll, image_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = getSupportActionBar();
        toolbar.setTitle(UserDetails.chatWith);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent i = getIntent();
        token = i.getStringExtra("token");

        prefManager = new PrefManager(getApplicationContext());
        my_token= prefManager.getFCMToken();

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        attachment = (ImageView)findViewById(R.id.attachment);

        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);


        //Create Reference for updating both users nodes on Firebase
        //When user sends messages
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://chat-app-ab12cd34.firebaseio.com/messages/" + prefManager.getSipNumber() + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://chat-app-ab12cd34.firebaseio.com/messages/" + UserDetails.chatWith + "_" + prefManager.getSipNumber() );

        System.out.println("Updating message record of both user: User 1(Sender): "+ "https://chat-app-ab12cd34.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        System.out.println("Updating message record of both user: User 2(Receiver): "+ "https://chat-app-ab12cd34.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    sendNotification(UserDetails.username, messageText);
                }
            }

        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachFile(ATTACHMENT_CHOICE_CHOOSE_IMAGE);

            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(message.contains("https://firebasestorage.")){
                    try {
                        includesForDownloadFiles(message);

                        if (userName.equals(UserDetails.username)) {
                            addMessageBox(message, 1,true);

                        }else{
                            addMessageBox( message, 2,true);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    if (userName.equals(UserDetails.username)) {
                        addMessageBox("You:-\n" + message, 1, false);
                    } else {
                        addMessageBox(UserDetails.chatWith + ":-\n" + message, 2, false);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
              //  Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //It will handle or shows text messages or image messages
    public void addMessageBox(String message, int type, Boolean isImageUrl){

        if(isImageUrl){
            imageView = new ImageView(Chat.this);

            try {
                Picasso.with(getApplicationContext()).load(message).into(imageView);

            }catch (Exception e){
                e.printStackTrace();
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.search_icon));
            }

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if(type == 1) {
                lp2.gravity = Gravity.RIGHT;
                imageView.setBackgroundResource(R.drawable.bubble_in);
            }
            else{
                lp2.gravity = Gravity.LEFT;
                imageView.setBackgroundResource(R.drawable.bubble_out);
            }
            imageView.setLayoutParams(lp2);
            layout.addView(imageView);
            scrollView.fullScroll(View.FOCUS_DOWN);
        }else{
            TextView textView = new TextView(Chat.this);

            textView.setText(message);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if(type == 1) {
                lp2.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.bubble_in);
            }
            else{
                lp2.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.bubble_out);
            }
            textView.setLayoutParams(lp2);
            layout.addView(textView);
            scrollView.fullScroll(View.FOCUS_DOWN);
        }

    }

    //Send notification to the specific user
    private void sendNotification(String user_name, String message){
        String url = "https://fcm.googleapis.com/fcm/send";
        System.out.println("Fetching Users : "+url);
        System.out.println("Token::: Chat screen: "+ token);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                doOnSuccess(s,message);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                Toast.makeText(getApplicationContext(),"Something went wrong.\n please try again later.",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("to", token);

                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("title", user_name);
                dataObject.addProperty("status", "1_"+my_token);
                dataObject.addProperty("message", message);


                jsonObject.add("data", dataObject);

                System.out.println("Notification params:: "+ jsonObject);
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> header = new HashMap<>();
                //Legacy Server Token : For sending notification
                header.put("Authorization", "key=Enter your Legacy Key");
                header.put("Content-Type", "application/json");
                return header;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
    }

    public void doOnSuccess(String s,String msg){
        try {
            JSONObject obj = new JSONObject(s);

            //Pushing messages to both references which updated messages on both nodes
            System.out.println("Notification send successfully : "+ s);
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", msg);
            map.put("user", UserDetails.username);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            messageArea.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void attachFile(final int attachmentChoice) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, getString(R.string.perform_action_with)),
                attachmentChoice);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult activityResult = ActivityResult.of(requestCode, resultCode, data);

            handleActivityResult(activityResult);

    }

    private void handleActivityResult(ActivityResult activityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {

            handlePositiveActivityResult(activityResult.requestCode, activityResult.data);
        } else {
            handleNegativeActivityResult(activityResult.requestCode);
        }
    }


    private void handlePositiveActivityResult(int requestCode, final Intent data) {
        switch (requestCode) {

            case ATTACHMENT_CHOICE_CHOOSE_IMAGE:
                final List<Uri> imageUris = AttachmentTool.extractUriFromIntent(data);

                String fileType = GlobalClass.getInstance().getMimeType(getApplicationContext(), imageUris.get(0));

                //To resolve video not uploading from gallery
                if (fileType.equals("mp4")) {
                    /*final String type = data == null ? null : data.getType();
                    for (Iterator<Uri> i = imageUris.iterator(); i.hasNext(); i.remove()) {
                        attachFileToConversation(conversation, i.next(), type);
                    }*/
                } else {
                    Uri selectedImageUri = data.getData();
                    Bitmap bitmap=null;
                    try {
                         bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }

                    uploadFile(bitmap,selectedImageUri);
                }

                break;

        }
    }

    private void handleNegativeActivityResult(int requestCode) {
        switch (requestCode) {
            //nothing to do for now
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


        //Uploading File on Firebase Storage
        private void uploadFile(Bitmap bitmap,Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(uri.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());

        StorageReference mountainImagesRef = storageRef.child("images/" + prefManager.getSipNumber() + "_" + UserDetails.chatWith + currentDateandTime+ ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
               // sendMsg("" + downloadUrl, 2);
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", downloadUrl.toString());
                map.put("user", UserDetails.username);
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                Log.d("downloadUrl-->", "" + downloadUrl);
                sendNotification(UserDetails.username, downloadUrl.toString());

            }
        });

    }


    ///Download received message in File format
    public void includesForDownloadFiles(String url) throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START download_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


        // [START download_to_memory]
        StorageReference islandRef = storageRef.child(url);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        // [END download_to_memory]

    }


}
