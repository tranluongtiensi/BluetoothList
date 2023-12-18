package org.dop.getbluetoothlist;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private List<Item> devices;
    private Context context;

    public ListAdapter(List<Item> devices, Context context) {
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bltName.setText(devices.get(position).getBltName());
        holder.bltAddress.setText(devices.get(position).getBltAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView bltName, bltAddress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bltName = itemView.findViewById(R.id.blt_name);
            bltAddress = itemView.findViewById(R.id.blt_address);
        }
    }

}
