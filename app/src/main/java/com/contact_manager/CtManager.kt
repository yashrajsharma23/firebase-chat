@file:Suppress("NAME_SHADOWING")

package com.contact_manager

//import eu.siacs.linphone.utils.DataManager
//import io.reactivex.android.schedulersAndroidSchedulers
import android.content.Context
import com.example.chatapp.model.PrefManager
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by mayur on 07/09/18.
 */

object CtManager {



    //Contact
    @JvmStatic
    fun fetchContact(context: Context,inserted: (List<MultiContact>) -> Unit){
        try {
            RxContacts.fetch(context)
//                .filter { m -> m.inVisibleGroup == 1 }
                    .toSortedList(RxContact::compareTo)
//                .observeOn(Schedulers.io())
//                .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe { ct ->

                        val list = ct.filter { !it.displayName.isNullOrEmpty() }
                                .filter { it.phoneNumbers.size > 0 }
                                .sortedBy {
                                    it.displayName.toLowerCase()
                                }
                                .map {

                                    val oldContacts: MultiContact? = getContactFromNumber(context, if (it.phoneNumbers.isNotEmpty()) it.phoneNumbers.first() else "")
                                    val olsIsFavContact: Boolean;
                                    if (oldContacts != null) {
                                        olsIsFavContact = oldContacts.isFavContact
                                    } else {
                                        olsIsFavContact = false
                                    }

                                    MultiContact().apply {
                                        contactId = it.id
                                        photoUri = it.thumbnail?.toString()
                                        labels = it.labels
                                        fullName = it.displayName
                                        contactNumber = if (it.phoneNumbers.isNotEmpty()) it.phoneNumbers.first() else ""
                                        numbers = it.phoneNumbers
                                        numbersArray = it.phoneNumbers.toString()
                                        isFavContact = olsIsFavContact
                                    }
                                }

                        // val detailList: ArrayList<MultiContact> = ArrayList()
                        val numberList: ArrayList<String> = ArrayList()
                        val pref = PrefManager(context)
                        numberList.add(pref.sipNumber)


                        Injection.provideCache(context).insert(list) {
                            inserted(list)

                        }

                    }
        } catch (ex: Exception) {
            print("Error in fetch contact")
        }

    }

    @JvmStatic
    fun getListOfContacts(context: Context) : List<MultiContact>? {
        return CybtelDatabase.getInstance(context).addressBookDao().contacts
    }
   /* @JvmStatic
    fun updateContactDatabase(context: Context, contactNumber:String,  isCybtelUser:Boolean,  cybtelUserName:String){
        CybtelDatabase.getInstance(context).addressBookDao().updateContactById(contactNumber,isCybtelUser,cybtelUserName)
    }*/

    @JvmStatic
    fun getContactFromNumber(context: Context, number:String): MultiContact? {
        return CybtelDatabase.getInstance(context).addressBookDao().getContactFromNumber(number)
    }

    @JvmStatic
    fun getChatAppContacts(context: Context) = CybtelDatabase.getInstance(context).addressBookDao().chatAppContacts


    @JvmStatic
    fun updateFavContact(context: Context, contactNumber:String,  isFavContact:Boolean){
        CybtelDatabase.getInstance(context).addressBookDao().updateFavContactByNumber(contactNumber,isFavContact)
    }

    @JvmStatic
    fun updateChatAppContact(context: Context, contactNumber:String,  isChatAppContact:Boolean){
        CybtelDatabase.getInstance(context).addressBookDao().updateChatAppContactByNumber(contactNumber,isChatAppContact)
    }



    @JvmStatic
    fun deleteContact(context: Context, phoneNumber:String){
        CybtelDatabase.getInstance(context).addressBookDao().deleteContact(phoneNumber)
    }

    @JvmStatic
    fun isChatAppContact(context: Context, phone:String): Boolean? {
        return CybtelDatabase.getInstance(context).addressBookDao().isChatAppContact(phone)
    }

}
