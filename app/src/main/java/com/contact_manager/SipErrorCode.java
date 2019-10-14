package com.contact_manager;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "sip_error_code_info")
public class SipErrorCode implements Comparable<SipErrorCode> {

    @PrimaryKey(autoGenerate = true)
    public int uniqueId = 0;

    @ColumnInfo(name = "sip_error_code")
    public String sip_error_code = "";

    @ColumnInfo(name = "sip_error_msg")
    public String sip_error_msg = "";

    @ColumnInfo(name = "sip_error_detail")
    public String sip_error_detail = "";

    public SipErrorCode(){
    }

    @Ignore
    public SipErrorCode(String sip_error_code,String sip_error_msg,String sip_error_detail){
        this.sip_error_code = sip_error_code;
        this.sip_error_msg = sip_error_msg;
        this.sip_error_detail = sip_error_detail;
    }


    public String getSip_error_code() {
        return sip_error_code;
    }

    public void setSip_error_code(String sip_error_code) {
        this.sip_error_code = sip_error_code;
    }

    public String getSip_error_msg() {
        return sip_error_msg;
    }

    public void setSip_error_msg(String sip_error_msg) {
        this.sip_error_msg = sip_error_msg;
    }

    public String getSip_error_detail() {
        return sip_error_detail;
    }

    public void setSip_error_detail(String sip_error_detail) {
        this.sip_error_detail = sip_error_detail;
    }


    @Override
    public int compareTo(@NonNull SipErrorCode sipErrorCode) {
        return 0;
    }
}
