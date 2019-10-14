package com.contact_manager;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Mayur on 07/09/18.
 */

/**
 * Room data access object for accessing the [Repo] table.
 */
@Dao
interface AddressBookDao {


    //SipErrorCode info
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSipError(SipErrorCode accountInfo);

    @Query("SELECT * FROM sip_error_code_info")
    public List<SipErrorCode> getSipErrorCount();

    @Query("SELECT * FROM sip_error_code_info WHERE sip_error_code =:errorCode")
    public List<SipErrorCode> getSipErrorInformation(String errorCode);

    @Query("DELETE FROM sip_error_code_info")
    public void truncateSipErrorCode();

    //SipAccountInfo info
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAccount(SipAccountInfo accountInfo);

    @Query("SELECT * FROM sip_account_info WHERE usernumber =:contactNumber")
    public SipAccountInfo getAccountInformation(String contactNumber);

    @Query("SELECT * FROM sip_account_info WHERE accounturi =:accountUri")
    public SipAccountInfo getAccountInformationFromUri(String accountUri);

    @Query("SELECT accounturi FROM sip_account_info WHERE id =:accountId")
    public String getAccountUriForAccId(int accountId);

    @Query("SELECT COUNT(usernumber) FROM sip_account_info WHERE usernumber =:contactNumber")
    public int checkAccountExist(String contactNumber);

    @Query("SELECT * FROM sip_account_info WHERE usernumber =:contactNumber")
    public List<SipAccountInfo> getAccountInformationList(String contactNumber);

    @Query("UPDATE sip_account_info SET accounturi = :accounturi, displayname =:displayname," +
            "usernumber =:contactNumber,passWord =:passWord,server_ip =:server_ip,"+
            "reg_uri =:reg_uri,proxies =:proxies,realm =:realm,"+
            "scheme =:scheme,dataType =:dataType,transport =:transport WHERE usernumber =:contactNumber")
    public void updateAccountInformation(String accounturi, String displayname, String contactNumber,
                                         String passWord, String server_ip, String reg_uri, String proxies,
                                         String realm, String scheme, int dataType, int transport);

    @Query("UPDATE sip_account_info SET isActive =:isActive WHERE accounturi =:accounturi")//" and usernumber =:contactNumber")
    public long updateSipStatus(String accounturi, boolean isActive);//,String contactNumber);

    @Query("SELECT isActive FROM sip_account_info WHERE usernumber =:contactNumber")
    public boolean getSipStatus(String contactNumber);

    @Query("DELETE FROM sip_account_info WHERE usernumber =:contactNumber")
    public void deleteAccount(String contactNumber);

    //CodecInfoDb Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCodecInformation(CodecInfoDb accountInfo);

  /*  @Query("UPDATE sip_account_info SET isActive =:isActive WHERE accounturi =:accounturi")//" and usernumber =:contactNumber")
    public long updateCodecInfo(String accounturi,boolean isActive);*/

    @Query("SELECT * FROM codec_info_db")
    public List<CodecInfoDb> getCodecList();

    //Multi_contact book
    @Query("DELETE FROM codec_info_db")
    public void truncateCodecDb();

    //Multi_contact book
    @Query("DELETE FROM multi_contacts")
    public void truncate();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> insert(List<MultiContact> posts);

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts:List<Contact>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDetail(posts:List<MultiContact>)

    @Query("DELETE FROM contacts WHERE isLocal = 0")
    fun truncate()

    @Query("DELETE FROM contacts WHERE isLocal = 1")
    fun truncateLocal()

    @Query("DELETE FROM multi_contacts")
    fun truncateDetail()

    // Do a similar query as the search API:
// Look for repos that contain the query string in the name or in the description
// and order those results descending, by the number of stars and then by name
    @Query("SELECT * FROM contacts WHERE isLocal = 0")
    fun reposByName():DataSource.Factory<Int, Contact>

    @Query("SELECT * FROM contacts WHERE isLocal = 1")

    fun localContacts():DataSource.Factory<Int, Contact>

    @Query("SELECT * FROM contacts WHERE isLocal = 0 AND ((contactFullName LIKE :queryString) OR (contactNumber LIKE :queryString)) ORDER BY id")

    fun reposByName(queryString:String):DataSource.Factory<Int, Contact>

    @Query("SELECT * FROM contacts WHERE (id LIKE :queryString)")

    fun getContactById(queryString:Long):Contact

    @Query("UPDATE contacts SET firstName=:firstName, lastName=:lastName, contactNumber=:contactNumber WHERE (id LIKE :id)")

    fun updateContactById(id:Long, firstName:String, lastName:String, contactNumber:String)

    @Query("SELECT contactFullName FROM contacts WHERE isLocal = 1 AND ((contactNumber GLOB :queryString1) OR (numbersToSearch LIKE :queryString2)) LIMIT 1")
    fun*/

  /*  @Query("UPDATE multi_contacts SET isCybtelUser=:isCybtelUser, cybtelUserName=:cybtelUserName WHERE contactNumber =:contactNumber")
    public void updateContactById(String contactNumber, Boolean isCybtelUser, String cybtelUserName);
*/


    @Query("UPDATE multi_contacts SET isFavContact=:isFavContact WHERE contactNumber =:contactNumber")
    public void updateFavContactByNumber(String contactNumber, Boolean isFavContact);

    @Query("UPDATE multi_contacts SET isChatAppContact=:isChatAppContact WHERE contactNumber =:contactNumber")
    public void updateChatAppContactByNumber(String contactNumber, Boolean isChatAppContact);

    @Query("SELECT contactNumber FROM multi_contacts")
    public List<String> getContactNumbers();

    @Query("SELECT * FROM multi_contacts")
    public List<MultiContact> getContacts();

    @Query("SELECT * FROM multi_contacts WHERE contactNumber =:contactNumber")
    public MultiContact getContactFromNumber(String contactNumber);


    @Query("SELECT * FROM multi_contacts WHERE id =:contactId")
    public MultiContact getContactFromId(Long contactId);

    @Query("SELECT * FROM multi_contacts WHERE cybtelUserName =:username")
    public MultiContact getContactFromUsername(String username);

    @Query("SELECT * FROM multi_contacts WHERE isFavContact=1")
    public List<MultiContact> getFavContacts();

    @Query("SELECT * FROM multi_contacts WHERE isChatAppContact=1")
    public List<MultiContact> getChatAppContacts();

    @Query("SELECT contactNumber FROM multi_contacts WHERE cybtelUserName =:username")
    public String getphoneFromUsername(String username);

    @Query("SELECT cybtelUserName FROM multi_contacts WHERE contactNumber =:phoneNumber")
    public String getUserNameFromPhone(String phoneNumber);

    @Query("SELECT photoUri FROM multi_contacts WHERE cybtelUserName =:username")
    public String getContactPicFromUsername(String username);

    @Query("UPDATE multi_contacts SET photoUri=:contactPic WHERE contactNumber =:contactNumber")
    public void updateContactPic(String contactNumber, String contactPic);

    @Query("SELECT cybtelUserName FROM multi_contacts")
    public List<String> getCybtelContactsUserName();

    @Query("DELETE FROM multi_contacts WHERE id =:contactId")
    public void deleteContact(Long contactId);


    @Query("DELETE FROM call_log")
    public void deleteCallLog();

    @Query("DELETE FROM call_log where remote_name =:uniqueid")
    public void deleteCallLogfromid(String uniqueid);

    //insert call log
    @Insert
    public void insertCallLog(CallHistory... callLog);

    @Query("SELECT * FROM call_log")
    public List<CallHistory> getCallLogs();

    @Query("SELECT * FROM call_log where login_user=:loginuser")
    public List<CallHistory> getCallLogs(String loginuser);

    @Query("SELECT * FROM call_log WHERE remote_name =:number")
    public CallHistory getCallLogForNumber(String number);

    @Query("SELECT * FROM call_log WHERE remote_name =:number")
    public List<CallHistory> getCallLogsForNumber(String number);

    //new
    @Insert
    public void insertAllCodecs(List<CodecInfoDb> callLog);

    @Query("DELETE FROM multi_contacts WHERE contactNumber =:phoneNumber")
    public void deleteContact(String phoneNumber);

    @Query("SELECT isChatAppContact FROM multi_contacts WHERE contactNumber =:contactNumber")
    public boolean isChatAppContact(String contactNumber);

    @Query("SELECT COUNT(contactNumber) FROM multi_contacts WHERE contactNumber =:contactNumber")
    public int checkNumberExist(String contactNumber);
}
