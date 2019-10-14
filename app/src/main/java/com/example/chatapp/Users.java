package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.contact_manager.CtManager;
import com.contact_manager.MultiContact;
import com.daimajia.swipe.SwipeLayout;
import com.example.chatapp.adapter.ContactFragmentAdapter;
import com.example.chatapp.model.GlobalClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Users extends Fragment  implements ValueEventListener {
    ListView usersList;
    UsersAdapter adapter;
    TextView noUsersText;
    ArrayList<String> al = new ArrayList<>();
    private List<MultiContact> multiContacts;
    int totalUsers = 0;
    ProgressDialog pd;
    Context ctx;
    HashMap<String, String> refrence_token_map = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_users, container, false);


        usersList = (ListView)view.findViewById(R.id.usersList);
        noUsersText = (TextView)view.findViewById(R.id.noUsersText);

        ctx= getContext();
        pd = new ProgressDialog(ctx);
        pd.setMessage("Loading...");
        //pd.show();

        showContacts();

        //To fetch all the registered user token from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("users").child("auth_token").addValueEventListener(this);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            try {
                showContacts();
            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    public void showContacts(){
        multiContacts = CtManager.getChatAppContacts(ctx);

        for(int i=0;i<multiContacts.size();i++){
            if(!multiContacts.get(i).contactNumber.equals(UserDetails.username)) {
                al.add(multiContacts.get(i).contactNumber);
                totalUsers++;
            }

        }

        //To remove duplicate values
            HashSet<String> hashSet = new HashSet<String>();
            hashSet.addAll(al);
            al.clear();
            al.addAll(hashSet);

        if(totalUsers < 1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            adapter= new UsersAdapter( al, ctx);
            usersList.setAdapter(adapter);
        }
    }


    public void doOnSuccess(String s){
        try {
            JSONObject obj = new JSONObject(s);

            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){
                key = i.next().toString();

                if(!key.equals(UserDetails.username)) {
                    al.add(key);
                }

                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(totalUsers <1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            //usersList.setAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, al));
            adapter= new UsersAdapter( al, ctx);
            usersList.setAdapter(adapter);
        }

        pd.dismiss();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
       // Get all the registered user token
        refrence_token_map.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            android.util.Log.d("DataSnapshot: ", "onDataChange: user_name_key: " + singleSnapshot.getKey());
            android.util.Log.d("DataSnapshot: ", "onDataChange: token: " + singleSnapshot.getValue());
            refrence_token_map.put(singleSnapshot.getKey(), Objects.requireNonNull(singleSnapshot.getValue()).toString());
            System.out.println("Token::: Mainactivity screen: onDataChange: "+ singleSnapshot.getValue());

        }
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    //When user click on specific chat item
    //It will fetch specific user token
    //for notification purpose when user sends chat at that time this token will be use
    public String getTokenFromUserName(String username) {
        String referenceToken = null;
        System.out.println("Token::: Mainactivity screen: getTokenFromUserName: "+ username + " refrence_token_map:"+refrence_token_map.size());

        if (refrence_token_map.size() > 0) {
            if (refrence_token_map.containsKey(username)) {
                referenceToken = refrence_token_map.get(username);
                System.out.println("Token::: Mainactivity screen: "+ referenceToken);

            }
        }
        return referenceToken;
    }


    public class UsersAdapter implements ListAdapter {
        ViewGroup parentView;
        View itemView;
        private ArrayList<String> mDataArray;
        private Context context;
        GlobalClass gc;

        public UsersAdapter(ArrayList<String> mDataArray, Context ctx){
            this.context= ctx;
            this.mDataArray= mDataArray;
            gc= GlobalClass.getInstance();
        }
        
        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            if (this.mDataArray != null) {
                return this.mDataArray.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return (long) mDataArray.get(position).hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            itemView = view;
            MyViewHolder holder;
            if (view==null){
                itemView = LayoutInflater.from(context)
                        .inflate(R.layout.contact_item, viewGroup, false);
                holder  = new MyViewHolder(itemView);
                itemView.setTag(holder);

            }else {
                holder = (MyViewHolder) itemView.getTag();
            }

            if (itemView == null) {
                parentView = new LinearLayout(context);
            } else {
                parentView = (LinearLayout) itemView;
            }

            try {

                String number = mDataArray.get(position);

                holder.nickNameView.setText(gc.getContactName(number, context));
                holder.numberView.setText(number);


                applyClickEvents(holder, position);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return itemView;
        }

        public class MyViewHolder /*extends RecyclerView.ViewHolder*/ {
            LinearLayout infoRowContainer;
            CircularImageView userImg;
            TextView nickNameView, numberView;


            public MyViewHolder(View view) {
//            super(view);
                infoRowContainer = (LinearLayout) view.findViewById(R.id.infoRowContainer);
                userImg = (CircularImageView) view.findViewById(R.id.userImg);
                nickNameView = (TextView) view.findViewById(R.id.nickNameView);
                numberView = (TextView) view.findViewById(R.id.numberView);

            }
        }


        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Nullable
        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private void applyClickEvents(final MyViewHolder holder, final int position) {

            holder.infoRowContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Open Specific chat passing that user's FCM token
                    UserDetails.chatWith = al.get(position);
                    String token = getTokenFromUserName(UserDetails.chatWith); //MainActivity.getInstance().
                    Intent i= new Intent(ctx,Chat.class);
                    i.putExtra("token",token);
                    System.out.println("Token::: Users screen: "+ token+" name:"+UserDetails.chatWith);
                    startActivity(i);
                }
            });
        }
    } 
}