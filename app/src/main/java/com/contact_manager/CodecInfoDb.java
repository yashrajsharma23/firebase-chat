package com.contact_manager;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "codec_info_db")
public class CodecInfoDb implements Comparable<CodecInfoDb> {

    @PrimaryKey(autoGenerate = true)
    public int uniqueId = 0;

    @ColumnInfo(name = "codecId")
    public String codecId = "";

    @ColumnInfo(name = "codec_desc")
    public String codec_desc = "";

    @ColumnInfo(name = "codec_priority")
    public String codec_priority = "";

    public CodecInfoDb(){

    }

    @Ignore
    public CodecInfoDb(String codecId, String codec_desc, String codec_priority){
        this.codecId = codecId;
        this.codec_desc = codec_desc;
        this.codec_priority = codec_priority;
    }


    public String getCodecId() {
        return codecId;
    }

    public void setCodecId(String codecId) {
        this.codecId = codecId;
    }

    public String getCodec_desc() {
        return codec_desc;
    }

    public void setCodec_desc(String codec_desc) {
        this.codec_desc = codec_desc;
    }

    public String getCodec_priority() {
        return codec_priority;
    }

    public void setCodec_priority(String codec_priority) {
        this.codec_priority = codec_priority;
    }



    @Override
    public int compareTo(@NonNull CodecInfoDb sipErrorCode) {
        return 0;
    }
}
