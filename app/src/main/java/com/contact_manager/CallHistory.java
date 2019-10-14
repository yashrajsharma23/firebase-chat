package com.contact_manager;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "call_log")
public class CallHistory implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uniqueId = 0;

    /*@ColumnInfo(name = "id")
    public Long callLogId =  0L;*/

    @ColumnInfo(name = "remote_name")
    public String remoteName = "";

    @ColumnInfo(name = "sip_uri")
    public String sipUri = "";

    @ColumnInfo(name = "date")
    public String date = "";

    @ColumnInfo(name = "direction")
    public String direction = "";

    @ColumnInfo(name = "duration")
    public long duration =0L;

    @ColumnInfo(name = "login_user")
    public String login_user = "";


}
