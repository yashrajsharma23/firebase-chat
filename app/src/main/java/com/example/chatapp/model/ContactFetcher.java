package com.example.chatapp.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactFetcher {

    private final Context context;
    GlobalClass gc;

    public ContactFetcher(Context c) {
        this.context = c;
        this.gc = GlobalClass.getInstance();
    }

    public static boolean deleteContact(Context ctx, String contactId,String phone, String name) {
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
