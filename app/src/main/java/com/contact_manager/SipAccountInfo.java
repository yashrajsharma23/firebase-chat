package com.contact_manager;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import java.io.Serializable;

@Entity(tableName = "sip_account_info")
public class SipAccountInfo implements Serializable {

    @Ignore
    public final static long INVALID_ID = -1;
    @Ignore
    public static final String CRED_SCHEME_DIGEST = "Digest";
    @Ignore
    public static final int CRED_DATA_PLAIN_PASSWD = 0;
    @Ignore
    public final static int TRANSPORT_AUTO = 0;
    @Ignore
    public final static int TRANSPORT_UDP = 1;
    @Ignore
    public final static int TRANSPORT_TCP = 2;
    @Ignore
    public final static int TRANSPORT_TLS = 3;
    @Ignore
    public String default_uri_scheme  = "sip";


    @PrimaryKey(autoGenerate = true)
    public int uniqueId = 0;

    @ColumnInfo(name = "id")
    public int accountId = -1;

    @ColumnInfo(name = "accounturi")
    public String accounturi = "";

    @ColumnInfo(name = "displayname")
    public String displayname = "";

    @ColumnInfo(name = "usernumber")
    public String usernumber = "";

    @ColumnInfo(name = "passWord")
    public String passWord = "";

    @ColumnInfo(name = "server_ip")
    public String server_ip = "";

    @ColumnInfo(name = "reg_uri")
    public String reg_uri = "";

    @ColumnInfo(name = "proxies")
    public String proxies = "";

    @ColumnInfo(name = "realm")
    public String realm = "";

    @ColumnInfo(name = "scheme")
    public String scheme = "";

    @ColumnInfo(name = "dataType")
    public int dataType = 0;

    @ColumnInfo(name = "transport")
    public int transport = 1;

    @ColumnInfo(name = "isActive")
    public boolean isActive = false;

    public SipAccountInfo(String accounturi , String displayname, String usernumber,String passWord,
                          String server_ip,String reg_uri,String proxies,String realm,String scheme,
                          int dataType,int transport) {
        this.accounturi = accounturi;
        this.displayname = displayname;
        this.usernumber = usernumber;
        this.passWord = passWord;
        this.server_ip = server_ip;
        this.reg_uri = reg_uri;
        this.proxies = proxies;
        this.realm = realm;
        this.scheme = scheme;
        this.dataType = dataType;
        this.transport = transport;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccounturi() {
        return accounturi;
    }

    public void setAccounturi(String accounturi) {
        this.accounturi = accounturi;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public String getReg_uri() {
        return reg_uri;
    }

    public void setReg_uri(String reg_uri) {
        this.reg_uri = reg_uri;
    }

    public String getProxies() {
        return proxies;
    }

    public void setProxies(String proxies) {
        this.proxies = proxies;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getTransport() {
        return transport;
    }

    public void setTransport(int transport) {
        this.transport = transport;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


}