package com.contact_manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;


/**
 * TODO Write javadoc
 *
 * @author Mayur Viras
 */
class ColumnMapper {

    // Utility class -> No instances allowed
    private ColumnMapper() {
    }

    static void mapInVisibleGroup(Cursor cursor, RxContact contact, int columnIndex) {
        contact.setInVisibleGroup(cursor.getInt(columnIndex));
    }

    static void mapDisplayName(Cursor cursor, RxContact contact, int columnIndex) {
        String displayName = cursor.getString(columnIndex);
        if (displayName != null && !displayName.isEmpty()) {
            contact.setDisplayName(displayName);
        }
    }

    static void mapCompanyName(Cursor cursor, RxContact contact, int columnIndex) {
        String companyName = cursor.getString(columnIndex);
        if (companyName != null && !companyName.isEmpty()) {
            contact.setCompanyName(companyName);
        }
    }

    static void mapEmail(Cursor cursor, RxContact contact, int columnIndex) {
        String email = cursor.getString(columnIndex);
        if (email != null && !email.isEmpty()) {
            contact.getEmails().add(email);
        }
    }

    static void mapPhoneNumber(Cursor cursor, RxContact contact, int columnIndex) {
        String phoneNumber = cursor.getString(columnIndex);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            // Remove all whitespaces
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            contact.getPhoneNumbers().add(phoneNumber);
        }
    }

    static void mapPhoto(Cursor cursor, RxContact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.setPhoto(Uri.parse(uri));
        }
    }

    static void mapStarred(Cursor cursor, RxContact contact, int columnIndex) {
        contact.setStarred(cursor.getInt(columnIndex) != 0);
    }

    static void mapThumbnail(Cursor cursor, RxContact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.equals("null") && uri.length() > 0) {
            contact.setThumbnail(Uri.parse(uri));
        }
    }

    static void mapNumberLabel(Context context, Cursor cursor, RxContact contact, int columnIndex) {
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            int typeId = cursor.getInt(columnIndex);
            String label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            String typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), typeId, label).toString();
            contact.getLabels().add(String.valueOf(typeLabel));
        }
    }

    static void mapNumberType(Context context, Cursor cursor, RxContact contact) {
        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));

//        Integer typeIdx = cursor.getInt(columnIndex);
            String typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, label).toString();


//        Integer label = ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(cursor.getInt(typeIdx));
//        if (label != null && !label.isEmpty()) {
            contact.getLabels().add(String.valueOf(typeLabel));
//        }
        }
    }

//    getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.TYPE));
}
