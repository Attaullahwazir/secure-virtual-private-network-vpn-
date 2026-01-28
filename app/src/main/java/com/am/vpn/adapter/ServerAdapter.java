package com.am.vpn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.am.vpn.R;
import com.am.vpn.model.VPNServer;

import java.util.List;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    private List<VPNServer> servers;
    private OnServerSelectedListener listener;

    public interface OnServerSelectedListener {
        void onServerSelected(VPNServer server);
    }

    public ServerAdapter(List<VPNServer> servers, OnServerSelectedListener listener) {
        this.servers = servers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        VPNServer server = servers.get(position);
        holder.serverName.setText(server.getName());
        holder.serverLocation.setText(server.getCity() + ", " + server.getCountry());
        
        if (server.isPremium()) {
            holder.premiumIcon.setVisibility(View.VISIBLE);
        } else {
            holder.premiumIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServerSelected(server);
            }
        });
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    static class ServerViewHolder extends RecyclerView.ViewHolder {
        TextView serverName;
        TextView serverLocation;
        ImageView premiumIcon;

        ServerViewHolder(View itemView) {
            super(itemView);
            serverName = itemView.findViewById(R.id.server_name);
            serverLocation = itemView.findViewById(R.id.server_location);
            premiumIcon = itemView.findViewById(R.id.premium_icon);
        }
    }
} 