package com.example.meetapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class ContactsGetter {

    private static Comparator<ContactItem> contactsComparator = new Comparator<ContactItem>() {

        @Override
        public int compare(ContactItem o1, ContactItem o2) {
            return o1.getContactName().compareTo(o2.getContactName());
        }
    };

    private static ArrayList<ContactItem> getContacts(Context context) {
        ArrayList<ContactItem> contacts = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[] {  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                                        null, null,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (managedCursor.moveToNext())
        {
            String name = managedCursor.getString
                    (managedCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber = managedCursor.getString
                    (managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new ContactItem(name, phoneNumber));
        }
        managedCursor.close();
        Collections.sort(contacts, contactsComparator);
        return contacts;
    }

    static void showContacts(Activity activity, Dialog addMembersDialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            RecyclerView contactRecyclerView = addMembersDialog.findViewById(R.id.contactsRecyclerView);
            contactRecyclerView.setHasFixedSize(true);
            contactRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            ArrayList<ContactItem> contacts = getContacts(activity);
            RecyclerView.Adapter contactAdapter = new ContactsAdapter(contacts);
            contactRecyclerView.setAdapter(contactAdapter);
        }
    }

}
