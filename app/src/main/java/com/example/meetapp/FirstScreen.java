package com.example.meetapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirstScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Group> groups = new ArrayList<>();
    private String userName = "Oren";
    private String phoneNumber = "972528240512";
    private DocumentSnapshot mLastQueriedDocument;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new GroupAdapter(groups, this);
        recyclerView.setAdapter(adapter);
        getGroups();
    }

    private void notifyDataSetChanged() {
        this.adapter.notifyDataSetChanged();
    }

    private void getGroups(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference groupsCollectionRef = db.collection("groups");
        Query userGroupsQuery = null;
        if(mLastQueriedDocument != null){
            userGroupsQuery = groupsCollectionRef
                    .whereArrayContains("members", phoneNumber)
                    .startAfter(mLastQueriedDocument);
        }
        else{
            userGroupsQuery = groupsCollectionRef
                    .whereArrayContains("members", phoneNumber);
        }

        userGroupsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document: task.getResult()){
                        Group group = document.toObject(Group.class);
                        groups.add(group);
                    }
                    if(task.getResult().size() != 0){
                        mLastQueriedDocument = task.getResult().getDocuments()
                                .get(task.getResult().size() -1);
                    }
                    notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }


}
