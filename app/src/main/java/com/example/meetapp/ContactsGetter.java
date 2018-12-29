package com.example.meetapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class ContactsGetter {

    static Comparator<ContactItem> contactsComparator = new Comparator<ContactItem>() {

        @Override
        public int compare(ContactItem o1, ContactItem o2) {
            return o1.getContactName().compareTo(o2.getContactName());
        }
    };

    static ArrayList<ContactItem> getContacts(Context context) {
        ArrayList<ContactItem> contacts = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);
            while (phoneCursor.moveToNext()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new ContactItem(name, phoneNumber));
                break;
            }
        }
        cursor.close();
        Collections.sort(contacts, contactsComparator);
        return contacts;
    }
}
