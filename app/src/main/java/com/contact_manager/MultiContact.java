package com.contact_manager;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "multi_contacts")
public class MultiContact implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uniqueId = 0;

    @ColumnInfo(name = "id")
    public Long contactId =  0L;

    public String firstName = "";

    public String lastName = "";

    public String fullName = "";

    public String cybtelUserName = "";

    public String sipAddress = "";

    public String searchText = "";

    public String contactNumber = "";


    public String numbersArray = "";



    @Ignore
    public Set<String> numbers = new HashSet<String>();

    @Ignore
    public ArrayList<String> labels = new ArrayList();

    public String label = "";

    public String photoUri = "";

    public Boolean isFavContact = false;

    public Boolean isChatAppContact = false;



}