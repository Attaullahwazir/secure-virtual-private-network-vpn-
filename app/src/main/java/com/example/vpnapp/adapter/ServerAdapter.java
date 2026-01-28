package com.example.vpnapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vpnapp.R;
import com.example.vpnapp.model.VPNServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {
    private List<VPNServer> servers;
    private List<VPNServer> filteredServers;
    private Context context;
    private OnServerSelectedListener listener;
    private Random random;

    public interface OnServerSelectedListener {
        void onServerSelected(VPNServer server);
    }

    public ServerAdapter(Context context, OnServerSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        this.servers = new ArrayList<>();
        this.filteredServers = new ArrayList<>();
        this.random = new Random();
    }

    public void setServers(List<VPNServer> servers) {
        this.servers = servers;
        this.filteredServers = new ArrayList<>(servers);
        // Simulate random ping and load values
        for (VPNServer server : this.servers) {
            server.setPing(20 + random.nextInt(100));
            server.setLoad(random.nextInt(100));
            server.setSpeed(50 + random.nextInt(450));
            server.setPremium(random.nextBoolean());
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredServers.clear();
        if (query.isEmpty()) {
            filteredServers.addAll(servers);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (VPNServer server : servers) {
                if (server.getName().toLowerCase().contains(lowerCaseQuery) ||
                    server.getCountry().toLowerCase().contains(lowerCaseQuery)) {
                    filteredServers.add(server);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_server, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        VPNServer server = filteredServers.get(position);
        
        // Set server name with premium indicator
        String serverName = server.getName();
        if (server.isPremium()) {
            serverName += " ⭐";
        }
        holder.serverName.setText(serverName);
        
        // Set country and ping
        holder.serverLocation.setText(server.getCountry());
        holder.pingTime.setText(server.getPing() + "ms");

        // Set server load
        holder.serverLoad.setText(server.getLoad() + "% Load");

        // Set country flag
        String countryCode = getCountryCode(server.getCountry());
        int flagResourceId = context.getResources().getIdentifier(
                "flag_" + countryCode.toLowerCase(),
                "drawable",
                context.getPackageName()
        );
        if (flagResourceId != 0) {
            holder.flagImage.setImageResource(flagResourceId);
        }

        // Set signal strength indicator based on server load
        int load = server.getLoad();
        if (load < 50) {
            holder.signalStrength.setImageResource(R.drawable.ic_signal_excellent);
            holder.signalStrength.setColorFilter(ContextCompat.getColor(context, R.color.green));
        } else if (load < 80) {
            holder.signalStrength.setImageResource(R.drawable.ic_signal_good);
            holder.signalStrength.setColorFilter(ContextCompat.getColor(context, R.color.accent));
        } else {
            holder.signalStrength.setImageResource(R.drawable.ic_signal_weak);
            holder.signalStrength.setColorFilter(ContextCompat.getColor(context, R.color.red));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServerSelected(server);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredServers.size();
    }

    private String getCountryCode(String countryName) {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale locale = new Locale("", countryCode);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return countryCode;
            }
        }
        return "unknown";
    }

    static class ServerViewHolder extends RecyclerView.ViewHolder {
        ImageView flagImage;
        TextView serverName;
        TextView serverLocation;
        TextView pingTime;
        ImageView signalStrength;
        TextView serverLoad;

        ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            flagImage = itemView.findViewById(R.id.flagImage);
            serverName = itemView.findViewById(R.id.serverName);
            serverLocation = itemView.findViewById(R.id.serverLocation);
            pingTime = itemView.findViewById(R.id.pingTime);
            signalStrength = itemView.findViewById(R.id.signalStrength);
            serverLoad = itemView.findViewById(R.id.serverLoad);
        }
    }
} 