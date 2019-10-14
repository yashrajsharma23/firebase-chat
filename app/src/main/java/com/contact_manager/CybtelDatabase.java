package com.contact_manager;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.chatapp.R;


/**
 * Created by Mayur on 07/09/18.
 */

@Database(entities = {MultiContact.class,SipAccountInfo.class,SipErrorCode.class, CodecInfoDb.class,CallHistory.class}, version = 1, exportSchema = false)
public abstract class CybtelDatabase extends RoomDatabase {
    public abstract AddressBookDao addressBookDao();

    private static CybtelDatabase INSTANCE = null;


    public static CybtelDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CybtelDatabase.class) {
                if (INSTANCE == null) {
                    return INSTANCE = buildDatabase(context);
                }
            }
        }

        return INSTANCE;
    }

    private static CybtelDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), CybtelDatabase.class, context.getResources().getString(R.string.app_name) + ".db").allowMainThreadQueries().build();
    }
}
