package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

class UsersGetter {

    private static List<User> getUsers(List<QuerySnapshot> usersList) {
        QuerySnapshot allUsersQuery = usersList.get(0);
        List<User> allUsers = allUsersQuery.toObjects(User.class);
        QuerySnapshot usersToSubtractQuery = usersList.get(1);
        List<User> usersToSubtract = usersToSubtractQuery.toObjects(User.class);
        allUsers.removeAll(usersToSubtract);
        return allUsers;
    }

    static void showUsers(Activity activity, Dialog addMembersDialog, List<QuerySnapshot> queriesList) {
        RecyclerView contactRecyclerView = addMembersDialog.findViewById(R.id.contactsRecyclerView);
        contactRecyclerView.setHasFixedSize(true);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        List<User> users = getUsers(queriesList);
        RecyclerView.Adapter contactAdapter = new ContactsAdapter(users);
        contactRecyclerView.setAdapter(contactAdapter);
    }
}
