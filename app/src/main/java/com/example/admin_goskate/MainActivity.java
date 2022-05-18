package com.example.admin_goskate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    ArrayList<Location> locationArrayList;
    MyAdapter myAdapter;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    final String[] collections = {"approval_shops", "approval_parks", "approval_spots"};
    final String regexTarget = "approval_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // region progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        // endregion

        // region recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // endregion

        firestore = FirebaseFirestore.getInstance();

        locationArrayList = new ArrayList<Location>();
        myAdapter = new MyAdapter(MainActivity.this, locationArrayList, recyclerView);

        // set adapter to recyclerView
        recyclerView.setAdapter(myAdapter);
        for (String collection: collections) {
            EventChangeListener(collection);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private void EventChangeListener(String collection) {
        // update view if the dataset is changed
        // WORKS if a new location is sent for approval
        // CRASHES app if the admin user rejects/approves a location; changes will go through on the database however,
        // results in admin users having to restart the app everytime they reject/approve one location
        firestore.collection(collection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        int i = 0;
                        if (error!=null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.e("fireStore error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // if data is added
                                Location currLocation = dc.getDocument().toObject(Location.class);
                                currLocation.setType(collection.replaceAll(regexTarget, ""));
                                currLocation.setDocumentID(value.getDocuments().get(i).getId());
                                locationArrayList.add(currLocation);
                            }
                            i++;
                        }
                        myAdapter.notifyDataSetChanged();
                        // hours wasted : 4
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }
    /*
    public void removeFromView() {
        // update the recycler view that a row was removed.
        // hours wasted : 4

        myAdapter.notifyDataSetChanged();
    }
     */
}






