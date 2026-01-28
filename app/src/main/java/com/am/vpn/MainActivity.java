package com.am.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.am.vpn.adapter.ServerAdapter;
import com.am.vpn.model.VPNServer;
import com.am.vpn.service.VPNService;
import com.am.vpn.util.ServerManager;

public class MainActivity extends AppCompatActivity implements ServerAdapter.OnServerSelectedListener {
    private static final int VPN_REQUEST_CODE = 0x0F;
    private ServerManager serverManager;
    private ServerAdapter serverAdapter;
    private VPNServer selectedServer;
    private Button connectButton;
    private TextView statusText;
    private RecyclerView serverList;
    private CircularProgressIndicator progressIndicator;

    private BroadcastReceiver vpnStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(VPNService.EXTRA_STATUS);
            updateStatus(status);
            
            long bytesIn = intent.getLongExtra(VPNService.EXTRA_BYTES_IN, -1);
            long bytesOut = intent.getLongExtra(VPNService.EXTRA_BYTES_OUT, -1);
            if (bytesIn != -1 && bytesOut != -1) {
                updateTrafficStats(bytesIn, bytesOut);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverManager = new ServerManager();
        setupUI();
        setupServerList();
        
        // Register for VPN status updates
        LocalBroadcastManager.getInstance(this).registerReceiver(
            vpnStatusReceiver,
            new IntentFilter(VPNService.ACTION_VPN_STATUS)
        );
    }

    private void setupUI() {
        connectButton = findViewById(R.id.connect_button);
        statusText = findViewById(R.id.status_text);
        serverList = findViewById(R.id.server_list);
        progressIndicator = findViewById(R.id.progress_indicator);

        connectButton.setOnClickListener(v -> {
            if (selectedServer == null) {
                Toast.makeText(this, "Please select a server first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (VPNService.isRunning()) {
                disconnectVPN();
            } else {
                connectVPN();
            }
        });
    }

    private void setupServerList() {
        serverAdapter = new ServerAdapter(serverManager.getServers(), this);
        serverList.setLayoutManager(new LinearLayoutManager(this));
        serverList.setAdapter(serverAdapter);
    }

    @Override
    public void onServerSelected(VPNServer server) {
        selectedServer = server;
        if (VPNService.isRunning()) {
            // Switch server while connected
            Intent intent = new Intent(this, VPNService.class);
            intent.setAction("SWITCH_SERVER");
            intent.putExtra("server_name", server.getName());
            startService(intent);
        }
    }

    private void connectVPN() {
        Intent vpnIntent = VpnService.prepare(this);
        if (vpnIntent != null) {
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        } else {
            startVPNService();
        }
    }

    private void disconnectVPN() {
        Intent intent = new Intent(this, VPNService.class);
        intent.setAction("STOP_VPN");
        startService(intent);
    }

    private void startVPNService() {
        Intent intent = new Intent(this, VPNService.class);
        intent.setAction("START_VPN");
        intent.putExtra("server_name", selectedServer.getName());
        startService(intent);
    }

    private void updateStatus(String status) {
        statusText.setText(status);
        if (status.contains("Connected")) {
            connectButton.setText("Disconnect");
            progressIndicator.setVisibility(View.GONE);
        } else if (status.contains("Connecting")) {
            connectButton.setText("Connecting...");
            progressIndicator.setVisibility(View.VISIBLE);
        } else {
            connectButton.setText("Connect");
            progressIndicator.setVisibility(View.GONE);
        }
    }

    private void updateTrafficStats(long bytesIn, long bytesOut) {
        // Update traffic statistics in the UI
        String stats = String.format("↑ %s/s ↓ %s/s",
            formatSpeed(bytesOut),
            formatSpeed(bytesIn));
        // Update your traffic stats TextView here
    }

    private String formatSpeed(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            startVPNService();
        } else {
            Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(vpnStatusReceiver);
        if (VPNService.isRunning()) {
            disconnectVPN();
        }
    }
} 