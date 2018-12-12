package com.example.meetapp;

import android.os.Build;
import android.support.annotation.RequiresApi;
import java.util.List;

public class Group {
    private String name;
    private String admin;
    private String groupId;
    private List<String> members;
    //    private @ServerTimestamp Date timestamp;
    private boolean isScheduled;

    public Group(String name, String admin, List<String> members, boolean isScheduled) {
        this.name = name;
//        this.timestamp = timestamp;
//        this.groupId = groupId;
        this.admin = admin;
        this.members = members;
//        this.participants = new String[]{};
        this.isScheduled = isScheduled;
    }

    public Group() {

    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMembers() {
        return String.join(",", members);
    }

}
