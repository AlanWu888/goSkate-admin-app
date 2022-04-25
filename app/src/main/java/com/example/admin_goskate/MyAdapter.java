package com.example.admin_goskate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Location> locationArrayList;

    public MyAdapter(Context context, ArrayList<Location> locationArrayList) {
        this.context = context;
        this.locationArrayList = locationArrayList;
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
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lbl_name, lbl_address, lbl_condition, lbl_type, lbl_description;
        Button btn_reject, btn_approve;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name);
            lbl_address = itemView.findViewById(R.id.lbl_address);
            lbl_condition = itemView.findViewById(R.id.lbl_condition);
            lbl_type = itemView.findViewById(R.id.lbl_type);
            lbl_description = itemView.findViewById(R.id.lbl_description);

            btn_approve = itemView.findViewById(R.id.btn_approve);
            btn_reject = itemView.findViewById(R.id.btn_reject);

            btn_approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: approve pressed on " + lbl_name.getText().toString());
                }
            });
            btn_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: reject pressed on " + lbl_name.getText().toString());
                }
            });
        }
    }
}
