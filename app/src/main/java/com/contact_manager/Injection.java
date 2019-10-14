package com.contact_manager;

import android.content.Context;

import java.util.concurrent.Executors;

/**
 * Created by Mayur on 07/09/18.
 */

public class Injection {
    /**
     * Creates an instance of [GithubLocalCache] based on the database DAO.
     */
    static LocalCache provideCache(Context context) {
        CybtelDatabase database = CybtelDatabase.getInstance(context);
        return new LocalCache(database.addressBookDao(), Executors.newSingleThreadExecutor());
    }

    /**
     * Creates an instance of [GithubRepository] based on the [GithubService] and a
     * [GithubLocalCache]
     *//*
    private fun provideGithubRepository(context: Context): AddressbookRepository {
        val API = AddressBookApi::class.java
        return AddressbookRepository(NetworkApi.getClient().create(API), provideCache(context))
    }

    *//**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * [ViewModel] objects.
     *//*
    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }*/
}
