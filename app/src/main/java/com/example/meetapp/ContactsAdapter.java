package com.example.meetapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<User> contactsList;

    static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView contactNameTextView;
        ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTextView = itemView.findViewById(R.id.userName);
        }
    }

    ContactsAdapter(List<User> contactsList){
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item,
                viewGroup, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i) {
        final User currentItem = contactsList.get(i);
        contactsViewHolder.contactNameTextView.setText(currentItem.getName());
        contactsViewHolder.contactNameTextView.setTag(currentItem.getUserId());
        contactsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMembersHandler.onContactClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
