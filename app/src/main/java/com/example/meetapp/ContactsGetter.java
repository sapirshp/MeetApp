package com.example.meetapp;

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
        Cursor managedCursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {
//                                    ContactsContract.CommonDataKinds.Phone._ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (managedCursor.moveToNext())
        {
//            String id = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber = managedCursor.getString(managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new ContactItem(name, phoneNumber));
        }
        managedCursor.close();
        
        Collections.sort(contacts, contactsComparator);
        return contacts;
    }
}
