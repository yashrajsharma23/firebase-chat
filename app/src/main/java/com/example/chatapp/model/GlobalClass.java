package com.example.chatapp.model;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.contact_manager.CtManager;
import com.contact_manager.MultiContact;
import com.example.chatapp.R;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GlobalClass {

    public static GlobalClass instance;


    public static List<String> favContactGetSets = new ArrayList<>();
    public static String accountStatus;
    public static int accountStatusCode;
    public static boolean isOnline;
    public static String currentStatus;

    public static boolean isFocusable;
    public static boolean isCardSaved;
    public static boolean Temp_value;

    public static GlobalClass getInstance() {
        if (instance == null) {
            instance = new GlobalClass();
        }
        return instance;
    }

    public static void hideSoftKeyboard(Activity context) {
        if (context.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean isKeyboardOpen(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        // First we check the locked state
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {
            // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
            // so we need to check if screen on for this case

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isLocked = !powerManager.isInteractive();
            } else {
                //noinspection deprecation
                isLocked = !powerManager.isScreenOn();
            }
        }

        // Log.d(String.format("Now device is %s.", isLocked ? "locked" : "unlocked"));
        return isLocked;
    }

    // to check internet connection
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getCurrentStatus() {
        return currentStatus;
    }

    public static void setCurrentStatus(String currentStatus) {
        GlobalClass.currentStatus = currentStatus;
    }


    //For setting Sip connection status
    public String getAccountStatus() {
        return accountStatus;
    }

    //For getting Sip connection status
    public void setAccountStatus(String accountStatus) {
        GlobalClass.accountStatus = accountStatus;
    }

    //For getting Sip connection status code
    public int getAccountStatusCode() {
        return accountStatusCode;
    }

    //For setting Sip connection status code
    public void setAccountStatusCode(int accountStatusCode) {
        GlobalClass.accountStatusCode = accountStatusCode;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        GlobalClass.isOnline = isOnline;
    }


    //To fetch contact_tab name from number
    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        try {
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                } else {
                    contactName = phoneNumber;
                }
                cursor.close();
            } else {
                contactName = phoneNumber;
            }
        } catch (Exception e) {
            contactName = phoneNumber;
            e.printStackTrace();
        }

        return contactName;
    }


    public String split_word(String label) {
        String x = label;

        String new_letter = "";
        String lastletter = null;
        String firstletter = x;
        try {


            //To remove special characters
            Pattern pt_1 = Pattern.compile("[^a-zA-Z0-9]");
            Matcher match_1 = pt_1.matcher(firstletter);

            if (match_1.find()) {
                while (match_1.find()) {
                    String s = match_1.group();
                    firstletter = firstletter.replaceAll("\\" + s, "");

                }
                //To get first character
                firstletter = firstletter.substring(0, 1);
                new_letter = firstletter;

            } else {
                //To get first character

                firstletter = x.substring(0, 1);
                new_letter = firstletter;

            }

            if (x.contains(" ")) {

                String lastword = x.substring(x.lastIndexOf(" "), x.length());

                //To remove special characters
                Pattern pt_2 = Pattern.compile("[^a-zA-Z0-9]");
                Matcher match_2 = pt_2.matcher(lastword);
                if (match_2.find()) {
                    while (match_2.find()) {
                        String s = match_2.group();
                        lastword = lastword.replaceAll("\\" + s, "");
                    }
                    for (int i = 0; i < lastword.length(); i++) {

                        lastletter = lastword.substring(1, 2);
                    }
                } else {
                    for (int i = 0; i < lastword.length(); i++) {

                        lastletter = lastword.substring(1, 2);
                    }
                }


                new_letter = firstletter.concat(lastletter);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_letter;
    }

    public TextDrawable name_image(String letter) {

        TextDrawable builder = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.parseColor("#ffffffff"))
                .withBorder(0)
                .width(150)  // width in px
                .height(150) // height in px
                .fontSize(64)
                .endConfig()
                .buildRoundRect(letter, Color.parseColor("#303F9F"), 150);

        return builder;
    }

    public TextDrawable name_image_for_drawer(String letter) {

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = R.color.app_header;

        TextDrawable builder = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.parseColor("#ffffffff"))//"#A0A0A0"))
                .useFont(Typeface.DEFAULT_BOLD)
                .withBorder(0)
                .width(150)  // width in px
                .height(150) // height in px
                .fontSize(64)
                .endConfig()
                //.buildRoundRect(letter, Color.WHITE, 150);
                .buildRoundRect(letter, color1, 150);

        return builder;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public String md5Encode(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);

            digest.update(s.getBytes());

            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<HashMap<String, String>> getNameFromNumberOnDialpad(String phoneNumber, Context context) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        String contactName = context.getResources().getString(R.string.add_contact);
        String contactNumber = "";
        List<MultiContact>  multiContacts = CtManager.getListOfContacts(context);
        HashMap<String, String> hm = new HashMap<>();
        for (MultiContact contact : multiContacts){
            if (contact.contactNumber.contains(phoneNumber)){
                contactName =contact.fullName;
                contactNumber = contact.contactNumber;
                break;
            }
        }
        hm.put("name", contactName);
        hm.put("number", contactNumber);
        arrayList.add(hm);

        return arrayList;

    }




    // hide soft input kaypad
    public void hideKeypad(Activity act) {
        // Check if no view has focus:
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public boolean getisFocusable() {
        return isFocusable;
    }

    public void setIsFocusable(boolean isFocusable) {
        GlobalClass.isFocusable = isFocusable;
    }

    public boolean getisCardSaved() {
        return isCardSaved;
    }

    public void setIsCardSaved(boolean isCardSaved) {
        GlobalClass.isCardSaved = isCardSaved;
    }

    public void set_temp_value(boolean temp_value) {
        GlobalClass.Temp_value = temp_value;
    }

    public boolean get_temp_value() {
        return Temp_value;
    }

    //To fetch contact_tab name from number
    public String getContactNameForRoaster(final String phoneNumber, Context context) {

        String contactName = "";

        MultiContact multiContact = CtManager.getContactFromNumber(context,phoneNumber);
        if (multiContact!=null) {
            contactName = multiContact.fullName;
        }else{
            contactName = phoneNumber;
        }

        return contactName;
    }

    public String getContactNameForGroup(final String phoneNumber, Context context, String loginUserNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        if (loginUserNumber.equals(phoneNumber)) {
            contactName = "You";
        } else {
            /*try {
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        contactName = cursor.getString(0);
                    } else {
                        contactName = "#";
                    }
                    cursor.close();
                } else {
                    contactName = "#";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
            MultiContact multiContact = CtManager.getContactFromNumber(context,phoneNumber);
            if (multiContact!=null) {
                contactName = multiContact.fullName;
            }else{
                contactName = "#";
            }
        }
        return contactName;
    }


    //Himadri
    //To check ContactBook exist on default book or not
    public boolean contactExists(Context context, String number) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    return true;
                }
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public TextDrawable name_image_for_conference(String letter) {

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();

        TextDrawable builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(0)
                .width(150)  // width in px
                .height(150) // height in px
                .fontSize(64)
                .endConfig()
                .buildRoundRect(letter, Color.parseColor("#303F9F"), 0);

        return builder;
    }

    public String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public String handleReplay(String body) {
        String messageTypePart;
        String messageSenderPart;
        String messageOldStringPart;
        String messageNewStringPart = "";


        try {
            String convertedString;

            if (body.startsWith("reply")) {
                //convertedString = String.valueOf(body).replace("##$$##&&##", "++");
                String[] messageType1 = String.valueOf(body).split("reply");
                String msgTypeAt0 = messageType1[1];
                if (msgTypeAt0.startsWith("##$$##&&##")) {
                    String str1 = msgTypeAt0.substring(10);
                    StringTokenizer st = new StringTokenizer(str1, "##$$##&&##");

                    messageTypePart = st.nextToken();
                    messageOldStringPart = st.nextToken();
                    messageSenderPart = st.nextToken();
                    messageNewStringPart = st.nextToken();

                    //messageNewStringPart = messageNewStringPart;

                }
            }else if(String.valueOf(body).contains("reply")){

                //convertedString = String.valueOf(body).replace("##$$##&&##", "++");
                String[] messageType1 = String.valueOf(body).split("reply");
                String msgTypeAt0 = messageType1[1];
                if (msgTypeAt0.startsWith("##$$##&&##")) {
                    String str1 = msgTypeAt0.substring(10);
                    StringTokenizer st = new StringTokenizer(str1, "##$$##&&##");

                    messageTypePart = st.nextToken();
                    messageOldStringPart = st.nextToken();
                    messageSenderPart = st.nextToken();
                    messageNewStringPart = st.nextToken();

                    //messageNewStringPart = messageNewStringPart;

                }

            }else{
                messageNewStringPart = body;
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageNewStringPart = body;
        }

        return messageNewStringPart;

    }


    public String getContactNameIfExists(final String phoneNumber, Context context) {

        String contactName = "";

        MultiContact multiContact = CtManager.getContactFromNumber(context,phoneNumber);
        if (multiContact!=null) {
            contactName = multiContact.fullName;
        }else{
            contactName = "";
        }
        return contactName;
    }

    public static boolean deleteContactNew(Context ctx, String contactId,String phone, String name) {
        if(phone != null && phone.length() >0){
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
            Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                            ctx.getContentResolver().delete(uri, null, null);
                            return true;
                        }

                    } while (cur.moveToNext());
                }

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            } finally {
                cur.close();
            }
            return false;
        }else{
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,contactId);
            int deleted = ctx.getContentResolver().delete(uri,null,null);

            if(deleted >0){
                return true;
            }else{
                return false;
            }

        }

    }


}
