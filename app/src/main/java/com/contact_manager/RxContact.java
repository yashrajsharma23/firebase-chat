package com.contact_manager;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class RxContact implements Comparable<RxContact> {

    RxContact(long id) {
        this.id = id;
    }

    final long id;
    Integer inVisibleGroup = 0;
    String displayName= "";
    String companyName = "";
    Boolean isStarred = false;
    Uri photo= null;
    Uri thumbnail = null;
    Set<String> emails = new HashSet();
    Set<String> phoneNumbers= new HashSet();
    ArrayList<String> labels  = new ArrayList();

    public long getId() {
        return id;
    }

    public Integer getInVisibleGroup() {
        return inVisibleGroup;
    }

    public void setInVisibleGroup(Integer inVisibleGroup) {
        this.inVisibleGroup = inVisibleGroup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getStarred() {
        return isStarred;
    }

    public void setStarred(Boolean starred) {
        isStarred = starred;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RxContact contact = (RxContact) o;
        return id == contact.id;
    }

    @Override
    public int compareTo(@NonNull RxContact other) {
        try {
            if (displayName == null || displayName.isEmpty()) {
                if (other.displayName == null || other.displayName.isEmpty()) {
                    return 0;               // BOth empty - so indicate
                }
                return 1;                   // empty string sorts last
            }
            if (other.displayName == null || other.displayName.isEmpty()) {
                return 0;                  // empty string sorts last
            }
            return displayName.compareTo(other.displayName);
        }catch (Exception ex){
            return 0;
        }
    }

}