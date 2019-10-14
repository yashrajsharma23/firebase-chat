package com.contact_manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static com.contact_manager.ColumnMapper.mapCompanyName;
import static com.contact_manager.ColumnMapper.mapDisplayName;
import static com.contact_manager.ColumnMapper.mapEmail;
import static com.contact_manager.ColumnMapper.mapInVisibleGroup;
import static com.contact_manager.ColumnMapper.mapNumberLabel;
import static com.contact_manager.ColumnMapper.mapPhoneNumber;
import static com.contact_manager.ColumnMapper.mapPhoto;
import static com.contact_manager.ColumnMapper.mapStarred;
import static com.contact_manager.ColumnMapper.mapThumbnail;


/**
 * Android contacts as rx observable.
 *
 * @author Zeeshan Saiyed
 *
 */
public class RxContacts extends ContentObserver {
    private static final String[] PROJECTION = {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.STARRED,
            ContactsContract.CommonDataKinds.Organization.COMPANY,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.IN_VISIBLE_GROUP,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.RawContacts.ACCOUNT_TYPE
    };

    private static final String SELECTION_ACCOUNT =ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.anddroid.contacts.sim' " + " AND " + ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'com.google' ";

    private ContentResolver mResolver;
    private Context mContext;

    private RxContacts(@NonNull Context context) {
        super(null);
        mContext = context;
        mResolver = context.getContentResolver();
    }

    public static Observable<RxContact> fetch(@NonNull final Context context) {
        return Observable.create(new ObservableOnSubscribe<RxContact>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull
                                          ObservableEmitter<RxContact> e) throws Exception {
                try {
                    new RxContacts(context).fetch(e);
                }catch (Exception ex){
                    Log.d("TAG", "subscribe:  error: "+ex);
                }
            }
        });
    }
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Log.d("RxContact", "[" +"Constants.CONTACTS_OBSERVER_SERVICE" + "] " + "Change in Contacts detected");
    }
    private void fetch(ObservableEmitter<RxContact> emitter) {
        HashMap<Long, RxContact> contacts = new HashMap<>();
        Cursor cursor = createCursor();
        cursor.moveToFirst();
        int idColumnIndex = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int inVisibleGroupColumnIndex = cursor.getColumnIndex(ContactsContract.Data.IN_VISIBLE_GROUP);
        int displayNamePrimaryColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int starredColumnIndex = cursor.getColumnIndex(ContactsContract.Data.STARRED);
        int photoColumnIndex = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int thumbnailColumnIndex = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int mimetypeColumnIndex = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int dataColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        int companyColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY);
        int labelType = cursor.getColumnIndex(ContactsContract.Data.DATA2);
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(idColumnIndex);
            RxContact contact = contacts.get(id);
            if (contact == null) {
                contact = new RxContact(id);
                mapInVisibleGroup(cursor, contact, inVisibleGroupColumnIndex);
                mapDisplayName(cursor, contact, displayNamePrimaryColumnIndex);
                mapStarred(cursor, contact, starredColumnIndex);
                mapPhoto(cursor, contact, photoColumnIndex);
                mapThumbnail(cursor, contact, thumbnailColumnIndex);
                contacts.put(id, contact);
            }
            String mimetype = cursor.getString(mimetypeColumnIndex);
            switch (mimetype) {
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                    mapEmail(cursor, contact, dataColumnIndex);
                    break;
                }
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                    mapPhoneNumber(cursor, contact, dataColumnIndex);
                    mapNumberLabel(mContext, cursor, contact, labelType);
                    break;
                }
                case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE: {
                    mapCompanyName(cursor, contact, dataColumnIndex);
                    break;
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        try {
            for (Long key : contacts.keySet()) {
                emitter.onNext(contacts.get(key));
            }
            emitter.onComplete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Cursor createCursor() {
        return mResolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Data.CONTACT_ID
        );
    }

}