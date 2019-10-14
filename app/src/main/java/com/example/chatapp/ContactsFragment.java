package com.example.chatapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.contact_manager.CtManager;
import com.contact_manager.MultiContact;
import com.example.chatapp.adapter.ContactFragmentAdapter;
import com.example.chatapp.model.ContactFetcher;
import com.example.chatapp.model.ContactItemInterface;
import com.example.chatapp.model.GlobalClass;
import com.example.chatapp.model.IndexableListView;
import com.example.chatapp.model.PrefManager;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


/**
 * Created by inextrix on 11/9/18.
 */

public class ContactsFragment extends Fragment implements TextWatcher,
        View.OnClickListener, ContactFragmentAdapter.ContactFragmentAdapterListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private String TAG = getClass().getSimpleName().toString();
    View view;
    IndexableListView listview;
    Boolean contactFetched = false;
    Activity activity;

    @BindView(R.id.input_search_query)
    EditText searchBox;

    @BindView(R.id.progressbar)
    ProgressBar prgs;

    @BindView(R.id.txt_error)
    TextView txt_error;

    @BindView(R.id.contact_title_txt)
    TextView contact_title_txt;

    @BindView(R.id.contact_title_icon)
    ImageView contact_title_icon;

    @BindView(R.id.refresh_icon)
    ImageView refreshIcon;

    @BindView(R.id.logout_icon)
    ImageView logout;

    private String searchString;
    private Object searchLock = new Object();
    boolean inSearchMode = false;
    List<MultiContact> listContacts;
    ArrayList<MultiContact> filterList;

    List<ContactItemInterface> contactList;
    List<ContactItemInterface> temContactList = new ArrayList<>();
    List<ContactItemInterface> filterListOld;
    private SearchListTask curSearchTask = null;
    private static ProgressDialog progressDialog;

    int type = 1;
    PrefManager pref;
    GlobalClass gc;
    Context ctx;
    String dialNumber;
    ContactFragmentAdapter adapter;

    private ProgressBar contactProgress;

    List<String> favContactList = new ArrayList<>();
    Unbinder unbinder;

    Boolean isFilterList = false;
    int isSync;
    ArrayList<HashMap<String,String>>AllNum2=new ArrayList<HashMap<String,String>>();

    public static ContactsFragment instance;

    public static ContactsFragment getInstance(){
        if (instance==null){
            return null;
        }
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.contacts_tab_layout_new, container, false);
        unbinder = ButterKnife.bind(this, view);

        instance =this;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        //progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }


        pref = new PrefManager(getActivity());
        gc = GlobalClass.getInstance();
        ctx = getActivity();
        contactProgress = (ProgressBar) view.findViewById(R.id.contact_progress);

        listview = (IndexableListView) view.findViewById(R.id.listview);
        listview.setInvisibleIndexer(false);
        listview.setFastScrollAlwaysVisible(true);
        listview.setSmoothScrollbarEnabled(true);
        filterList = new ArrayList<>();
        contactList = new ArrayList<>();

        searchBox.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });

        searchBox.addTextChangedListener(this);
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), refreshIcon);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.refresh_contact_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        contactProgress.setVisibility(View.VISIBLE);
                        refreshContact();
                        return true;
                    }
                });
                popup.show();//showing popup menu

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.setLoggedIn("false");
                pref.setSipNumber("");

                startActivity(new Intent(ctx, Login.class));
                getActivity().finish();
            }
        });

        try {

            IntentFilter iFilter_NorecordFound = new IntentFilter("contact.no_record_found");
            getActivity().registerReceiver(mNoRecordFound, iFilter_NorecordFound);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    private BroadcastReceiver mNoRecordFound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("contact.no_record_found")) {
                String flag = intent.getExtras().getString("flag");
                if (flag.equals("1")) {
                    txt_error.setVisibility(View.VISIBLE);
                } else {
                    txt_error.setVisibility(View.INVISIBLE);
                }
            }
        }
    };


    ///Show all the stored contacts in DB
    public void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            txt_error.setVisibility(View.GONE);
        } else {
            listContacts = CtManager.getListOfContacts(getActivity());
            if (listContacts.size() > 0) {
                searchBox.setVisibility(View.VISIBLE);
                txt_error.setVisibility(View.GONE);
                adapter = new ContactFragmentAdapter(ctx, listContacts, ContactsFragment.this, ContactsFragment.this);
                listview.setAdapter(adapter);

            } else {
                searchBox.setVisibility(View.GONE);
                txt_error.setVisibility(View.VISIBLE);
            }
        }
    }


    //When user refresh the contacts
    class ContactAsynk extends AsyncTask<String, String, String> {

        List<MultiContact> listContacts;
        @Override
        protected String doInBackground(String... strings) {
            contactList.clear();
            try {
                CtManager.fetchContact(getContext(), contacts -> {
                    return null;
                });

            }catch (Exception e){
                Log.d("Login:: ", "onCreate: error in contact Sync:: "+e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            contactProgress.setVisibility(View.GONE);

            listContacts = CtManager.getListOfContacts(getContext());
            MainActivity.getInstance().fetchChatContact();

            if (listContacts.size() > 0) {
                searchBox.setVisibility(View.VISIBLE);
                txt_error.setVisibility(View.GONE);
                adapter = new ContactFragmentAdapter(ctx, listContacts, ContactsFragment.this, ContactsFragment.this);
                listview.setAdapter(adapter);

            } else {
                searchBox.setVisibility(View.GONE);
                txt_error.setVisibility(View.VISIBLE);
            }
        }
    }

    public void autoUpdateContact(){
        new ContactAsynk().execute("Run");

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        boolean permission_st = false;
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                permission_st = true;
            } else {
                permission_st = false;
            }
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                permission_st = true;
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
            if (permission_st) {
                /*if(contaSync==true) {
                    showContacts();
                }*/
                showContacts();
            } else {
                txt_error.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d("Contact", "ActivityLifeCycle ContactFragment onResume: Top");
        showContacts();
        Log.d("Contact", "ActivityLifeCycle ContactFragment onResume: End");
    }

    @Override
    public void onDestroyView() {
        try {
            gc.hideSoftKeyboard(getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (adapter != null) {
            adapter.getFilter().filter(s);
//            listview.setIndexBarVisibility(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }



    //Delete the selected Contacts
    public void delete_call(MultiContact multiContact, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setIcon(R.drawable.im);
        alert.setTitle("Alert");
        alert.setCancelable(false);
        alert.setMessage(R.string.warning_delete);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (multiContact != null) {
                    listContacts.remove(multiContact);
                    adapter.notifyDataSetChanged();
                    CtManager.deleteContact(getActivity(),multiContact.contactNumber);

                    try {

                        boolean deleted = new ContactFetcher(getActivity()).deleteContact(getActivity(),String.valueOf(multiContact.contactId), multiContact.contactNumber, multiContact.fullName);

                        if (!deleted){
                            Toast.makeText(getActivity(), "Oops!!Some problem occurred on delete.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Oops!!Some problem occurred on delete.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }


    //onClick of list item
    @Override
    public void onClick(View view) {
        List<MultiContact> searchList = listContacts;
        String name = searchList.get((Integer) view.getTag()).fullName;
        long id = searchList.get((Integer) view.getTag()).contactId ;
        String number =  "";
    }


    @Override
    public void onEditContact(int position, List<MultiContact> contactList) {
        List<MultiContact> searchList = contactList;

        String name = searchList.get(position).fullName;
        long id = searchList.get(position).contactId;
        String number =  "";
        if (searchList.get(position).contactNumber !=null) {
            number = searchList.get(position).contactNumber;

        }

        Intent intent1 = new Intent(Intent.ACTION_EDIT);
        intent1.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));
        startActivity(intent1);

    }


    //onClick of Row list item
    @Override
    public void onItemRowClicked(int position,List<MultiContact> listContacts) {
        List<MultiContact> searchList = listContacts;
        try {
            CustomDialogClass customDialog = new CustomDialogClass(getActivity(),searchList.get(position), position);
            customDialog.show();

        }catch (Exception e){
            e.printStackTrace();

        }

    }

    //onClick of Invite button
    @Override
    public void onInviteClick(int position,List<MultiContact> listContacts) {
        List<MultiContact> searchList = listContacts;
        String number = "";
        shareOnOtherSocialMedia(getActivity());
    }

    //onClick of Chat to redirect it to personal chat screen
    @Override
    public void onChatClick(int position,List<MultiContact> listContacts) {
        List<MultiContact> searchList = listContacts;

        String number = "";
        if (searchList.get(position).contactNumber!=null) {
            number = searchList.get(position).contactNumber;

        }
        UserDetails.chatWith = number;
        startActivity(new Intent(ctx, Chat.class));
    }


    private class SearchListTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            filterList.clear();
            String keyword = params[0];
            inSearchMode = (keyword.length() > 0);
            if (inSearchMode) {
                for (MultiContact item : listContacts) {
                    MultiContact contact = (MultiContact) item;

                    if ((contact.getClass().getName().toUpperCase().indexOf(keyword) > -1)) {
                        filterList.add(item);
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            synchronized (searchLock) {
                if (inSearchMode) {
                    if (filterList != null) {

                        try {
                            txt_error.setVisibility(View.GONE);
                            listview.setVisibility(View.VISIBLE);
                            prgs.setVisibility(View.GONE);

//                            initialiseUI(filterList);
                            isFilterList = true;
                            if (filterList.size() > 0) {
                                txt_error.setVisibility(View.GONE);
                                listview.setVisibility(View.VISIBLE);

                                adapter = new ContactFragmentAdapter(ctx, filterList, ContactsFragment.this, ContactsFragment.this);
                                listview.setAdapter(adapter);
                            } else {

                                listview.setVisibility(View.GONE);
                                txt_error.setVisibility(View.VISIBLE);
                                txt_error.setText(getResources().getString(R.string.no_record_found));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                } else {

                    if (temContactList != null) {
                        try {
                            txt_error.setVisibility(View.GONE);
                            listview.setVisibility(View.VISIBLE);
                            prgs.setVisibility(View.GONE);

                            isFilterList = false;

                            if(temContactList.size()>0) {
                                txt_error.setVisibility(View.GONE);
                                listview.setVisibility(View.VISIBLE);

                                adapter = new ContactFragmentAdapter(ctx, filterList, ContactsFragment.this, ContactsFragment.this);
                                listview.setAdapter(adapter);
                            }else{

                                listview.setVisibility(View.GONE);
                                txt_error.setVisibility(View.VISIBLE);
                                txt_error.setText(getResources().getString(R.string.no_record_found));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        }
    }




    @OnClick(R.id.contact_title_icon)
    public void onContactAdd() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    public class CustomDialogClass extends Dialog implements View.OnClickListener {

        public Context c;
        public Dialog d;
        public TextView btn_call, btn_delete;
        public RelativeLayout dialButton_delete, dialButton_call;
        String id,name, number, photo,flag;
        int position;

        CircularImageView imUser;
        CircularImageView imUser2;
        MultiContact multiContact;

        public CustomDialogClass(Context a, MultiContact multiContact, int position) {
            super(a);
            this.c = a;
            this.id = String.valueOf(multiContact.contactId);
            this.name = multiContact.fullName;
            this.number = multiContact.contactNumber;
            this.position = position;
            this.photo = multiContact.photoUri;
            this.multiContact =multiContact;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.contact_custom_dialog);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txttitle = (TextView) findViewById(R.id.txttile);
            TextView txtNumber = (TextView) findViewById(R.id.txtNumber);

            txttitle.setText(this.name);
            if (TextUtils.isEmpty(this.number)){
                txtNumber.setText(this.name);
            }else {
                txtNumber.setText(this.number);
            }

            imUser = (CircularImageView) findViewById(R.id.userImg);
            imUser2 = (CircularImageView) findViewById(R.id.userImg_2);

            imUser.setVisibility(View.VISIBLE);
            imUser2.setVisibility(View.GONE);



            /*if (photo ==null){*/
            if (photo ==null){
                String imagePlaceholder;
                if (multiContact.fullName.split(" ").length > 1) {
                    imagePlaceholder = multiContact.fullName.split(" ")[0].substring(0, 1) + multiContact.fullName.split(" ")[multiContact.fullName.split(" ").length ].substring(0, 1);
                } else {
                    imagePlaceholder = multiContact.fullName.split(" ")[0].substring(0, 1);
                }
                photo =imagePlaceholder;
            }
            System.out.println("Contacts Testing:: onCreate "+ photo);
            TextDrawable tw = gc.name_image(photo);

            Picasso.with(ctx)
                    .load(photo)
                    .placeholder(tw)
                    .error(tw)
                    .fit().centerCrop()
                    .into(imUser);


            dialButton_call = (RelativeLayout) findViewById(R.id.dialButton_call);
            dialButton_delete = (RelativeLayout) findViewById(R.id.dialButton_delete);
            btn_call = (TextView) findViewById(R.id.btn_call);
            btn_delete = (TextView) findViewById(R.id.btn_delete);
            dialButton_call.setOnClickListener(this);
            dialButton_delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.dialButton_delete:
                    delete_call(multiContact,position);
                    break;

                default:
                    break;
            }
            dismiss();
        }
    }

    private void showDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.refresh_contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_contact:
                contactProgress.setVisibility(View.VISIBLE);
//                refreshContact();
                break;
        }
        return true;

    }
    public void refreshContact(){
        CtManager.fetchContact(getActivity(), contacts -> {
            if (ContactsFragment.getInstance() != null) {
                ContactsFragment.getInstance().autoUpdateContact();
            }
            return null;
        });
    }
    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    public void showProgress(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contactProgress.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
//        instance =null;
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


    public static boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;


        return isKeyboardShown;
    }

    public static void shareOnOtherSocialMedia(Context ctx) {

        String sAux;
        List<Intent> shareIntentsLists = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        sAux = "\nLet me recommend you this application\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=com.dialer.calmax&hl=en \n\n";

        List<ResolveInfo> resInfos = ctx.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (android.content.pm.ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                if (!packageName.toLowerCase().contains("calmax")) {
                    Intent intent = new Intent();
                    intent.setComponent(new android.content.ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.setPackage(packageName);
                    intent.putExtra(Intent.EXTRA_SUBJECT, String.format(ctx.getResources().getString(R.string.app_name)));
                    intent.putExtra(Intent.EXTRA_TEXT, sAux);
                    shareIntentsLists.add(intent);
                }
            }
            if (!shareIntentsLists.isEmpty()) {
                Intent chooserIntent = Intent.createChooser(shareIntentsLists.remove(0), "Choose app to share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toArray(new android.os.Parcelable[]{}));
                //chooserIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(ctx.getResources().getString(R.string.app_name)));
                //chooserIntent.putExtra(Intent.EXTRA_TEXT, sAux);
                ctx.startActivity(chooserIntent);
            } else
                Log.e("Error", "No Apps can perform your task");

        }
    }


}


