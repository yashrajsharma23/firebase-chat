package com.contact_manager

import android.util.Log
import java.util.concurrent.ExecutorService

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
internal class LocalCache(private val addressBookDao: AddressBookDao, private val ioExecutor: ExecutorService) {

    /**
     * Insert a list of repos in the database, on a background thread.
     */
    fun clear() {
        ioExecutor.execute { addressBookDao.truncate() }
    }


    /**
     * Insert a list of repos in the database, on a background thread.
     */
    fun insert(repos: List<MultiContact>, inserted: () -> Unit) {
        ioExecutor.execute {
            addressBookDao.truncate()
            val insertedId = addressBookDao.insert(repos)
            inserted()
            Log.e("@@", "InsertedId : $insertedId")
        }
    }


    /**
     * Insert a list of repos in the database, on a background thread.
     */
    /*fun insertSipError(repos: List<SipErrorCode>){//}, inserted: () -> Unit) {
        ioExecutor.execute {
            addressBookDao.truncateSipErrorCode()
            for (i in 0 until repos.size) {
                val insertedId = addressBookDao.insertSipError(repos.get(i))

            }


        }
    }*/
}