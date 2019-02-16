package com.example.meetapp;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

class AddMembersHandler {
    private static Dialog addMembersDialog;
    private static ArrayList<String> membersNamesToAdd = new ArrayList<>();
    private static ArrayList<String> membersIdsToAdd = new ArrayList<>();

    static void setDialog(Dialog dialog){
        addMembersDialog = dialog;
    }

    static void onContactClick(View v){
        RelativeLayout contactLayout = v.findViewById(R.id.contactLayout);
        if (contactLayout.getTag() != "chosen") {
            clickOn(v);
        }
        else{
            clickOff(v);
        }
    }

    private static void clickOn(View view){
        RelativeLayout contactLayout = view.findViewById(R.id.contactLayout);
        TextView contactName = view.findViewById(R.id.userName);
        ImageButton okButton = addMembersDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.green_round_background);
        contactLayout.setBackgroundColor(view.getResources().getColor(R.color.colorGreen));
        contactLayout.setTag("chosen");
        addMember((String) contactName.getText(), (String) contactName.getTag());
    }

    private static void clickOff(View view){
        RelativeLayout contactLayout = view.findViewById(R.id.contactLayout);
        TextView contactName = view.findViewById(R.id.userName);
        ImageButton okButton = addMembersDialog.findViewById(R.id.chooseMembers);
        removeMember((String) contactName.getText(), (String) contactName.getTag());
        if (isMemberToAddEmpty()){
            okButton.setBackgroundResource(R.drawable.disabled_button_background);
        }
        contactLayout.setBackgroundColor(view.getResources().getColor(R.color.colorWhite));
        contactLayout.setTag("notChosen");
    }

    static void chooseMembers(final Runnable onChooseMembers){
        ImageButton okBtn = addMembersDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembersDialog.dismiss();
                onChooseMembers.run();
                AddMembersHandler.clearMembersToAdd();
            }
        });
    }

    private static void addMember(String memberName, String memberId) {
        membersNamesToAdd.add(memberName);
        membersIdsToAdd.add(memberId);
    }

    private static boolean isMemberToAddEmpty(){
        return membersNamesToAdd.isEmpty();
    }

    private static void removeMember(String member, String memberId){
        membersNamesToAdd.remove(member);
        membersIdsToAdd.remove(memberId);
    }

    static ArrayList<String> getMembersNamesToAdd(){
        return membersNamesToAdd;
    }

    static ArrayList<String> getMembersIdsToAdd(){
        return membersIdsToAdd;
    }

    static void clearMembersToAdd(){
        membersNamesToAdd.clear();
        membersIdsToAdd.clear();
    }
}
