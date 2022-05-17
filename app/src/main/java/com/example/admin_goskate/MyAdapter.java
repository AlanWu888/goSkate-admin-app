package com.example.admin_goskate;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Location> locationArrayList;
    RecyclerView rv;

    public MyAdapter(Context context, ArrayList<Location> locationArrayList, RecyclerView rv) {
        this.context = context;
        this.locationArrayList = locationArrayList;
        this.rv = rv;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Location location = locationArrayList.get(position);
        holder.lbl_name.setText(location.name);
        holder.lbl_address.setText(location.address);
        holder.lbl_condition.setText(location.condition);
        holder.lbl_description.setText(location.description);
        holder.lbl_type.setText(location.type);
        holder.lbl_id.setText(location.documentID);
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView lbl_name, lbl_address, lbl_condition, lbl_type, lbl_description, lbl_id;
        Button btn_reject, btn_approve;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name);
            lbl_address = itemView.findViewById(R.id.lbl_address);
            lbl_condition = itemView.findViewById(R.id.lbl_condition);
            lbl_type = itemView.findViewById(R.id.lbl_type);
            lbl_description = itemView.findViewById(R.id.lbl_description);
            lbl_id = itemView.findViewById(R.id.lbl_id);

            btn_approve = itemView.findViewById(R.id.btn_approve);
            btn_reject = itemView.findViewById(R.id.btn_reject);

            btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Log to the console - test onClick works
                    Log.d("TAG", "onClick: approve pressed on " + lbl_name.getText().toString());

                    String name, locationName, description, condition, collectionType, documentID;
                    name = lbl_name.getText().toString();
                    locationName = lbl_address.getText().toString();
                    description = lbl_description.getText().toString();
                    collectionType = lbl_type.getText().toString();
                    condition = lbl_condition.getText().toString();
                    documentID = lbl_id.getText().toString();

                    // try to get LatLng from the address, then add to fireStore
                    Geocoder geoCoder = new Geocoder(view.getContext(), Locale.getDefault());
                    Log.d("TAG", "onSuccess: components defined");
                    try {
                        List<Address> address = geoCoder.getFromLocationName(locationName, 1);
                        double latitude = address.get(0).getLatitude();
                        double longitude = address.get(0).getLongitude();

                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("geolocation", new GeoPoint(latitude, longitude));
                        map.put("description", description);

                        if (!collectionType.equals("shops")) {
                            map.put("condition", condition);
                        }
                        map.put("address", locationName);

                        Log.d("TAG", "items put into hashmap");

                        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                        fireStore.collection(collectionType).add(map)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("TAG", "onSuccess: task successful");
                                        // remove document from fireStore
                                        removeDocument(documentID, collectionType);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "onFailure: task failed");
                                        Log.d("TAG", "onFailure 2: " + e);
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("TAG", e.toString());
                    }
                }
            });

            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: reject pressed on " + lbl_name.getText().toString());
                    String collectionType, documentID;
                    collectionType = lbl_type.getText().toString();
                    documentID = lbl_id.getText().toString();

                    // remove document from fireStore
                    removeDocument(documentID, collectionType);

                }
            });
        }

        private void removeDocument(String documentID, String collection) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference noteRef = firebaseFirestore.collection("approval_"+collection).document(documentID);

            noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Successfully moved from approvals", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // new MainActivity().removeFromView();
        }
    }
}
