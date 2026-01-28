package com.am.vpn.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import androidx.core.app.NotificationCompat;

import com.am.vpn.MainActivity;
import com.am.vpn.R;
import com.am.vpn.model.VPNServer;
import com.am.vpn.util.ServerManager;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class VPNService extends VpnService {
    private static final String CHANNEL_ID = "VPNServiceChannel";
    public static final String ACTION_VPN_STATUS = "vpn_status";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_BYTES_IN = "bytes_in";
    public static final String EXTRA_BYTES_OUT = "bytes_out";

    private static boolean isRunning = false;
    private ServerManager serverManager;
    private ParcelFileDescriptor vpnInterface;
    private Thread vpnThread;
    private Handler handler;
    private AtomicLong bytesIn = new AtomicLong();
    private AtomicLong bytesOut = new AtomicLong();

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serverManager = new ServerManager();
        handler = new Handler();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "START_VPN":
                        String serverName = intent.getStringExtra("server_name");
                        startVPN(serverName);
                        break;
                    case "STOP_VPN":
                        stopVPN();
                        break;
                    case "SWITCH_SERVER":
                        String newServer = intent.getStringExtra("server_name");
                        switchServer(newServer);
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void startVPN(String serverName) {
        try {
            VPNServer selectedServer = serverManager.getServerByName(serverName);
            if (selectedServer == null) {
                broadcastStatus("Server not found");
                return;
            }

            Builder builder = new Builder();
            builder.setSession("AM VPN")
                   .addAddress("10.0.0.2", 32)
                   .addDnsServer("8.8.8.8")
                   .addDnsServer("8.8.4.4")
                   .addRoute("0.0.0.0", 0);

            vpnInterface = builder.establish();
            if (vpnInterface == null) {
                broadcastStatus("Failed to establish VPN connection");
                return;
            }

            isRunning = true;
            broadcastStatus("Connected to " + selectedServer.getName());
            startNetworkThread(selectedServer);
        } catch (Exception e) {
            broadcastStatus("Error: " + e.getMessage());
            stopVPN();
        }
    }

    private void startNetworkThread(VPNServer server) {
        vpnThread = new Thread(() -> {
            try {
                ByteBuffer packet = ByteBuffer.allocate(32767);
                while (!Thread.interrupted()) {
                    int len = vpnInterface.read(packet.array(), 0, packet.capacity());
                    if (len > 0) {
                        bytesIn.addAndGet(len);
                        // Process packet here
                        vpnInterface.write(packet.array(), 0, len);
                        bytesOut.addAndGet(len);
                    }
                }
            } catch (Exception e) {
                broadcastStatus("VPN tunnel error: " + e.getMessage());
                stopVPN();
            }
        });
        vpnThread.start();
    }

    private void broadcastStatus(String status) {
        Intent intent = new Intent(ACTION_VPN_STATUS);
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_BYTES_IN, bytesIn.get());
        intent.putExtra(EXTRA_BYTES_OUT, bytesOut.get());
        sendBroadcast(intent);
    }

    private void switchServer(String newServer) {
        stopVPN();
        handler.postDelayed(() -> startVPN(newServer), 1000);
    }

    private void stopVPN() {
        isRunning = false;
        if (vpnThread != null) {
            vpnThread.interrupt();
            vpnThread = null;
        }

        if (vpnInterface != null) {
            try {
                vpnInterface.close();
            } catch (Exception e) {
                broadcastStatus("Error closing VPN interface: " + e.getMessage());
            }
            vpnInterface = null;
        }

        broadcastStatus("Disconnected");
        stopForeground(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "AM VPN Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("AM VPN connection status notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(VPNServer server) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AM VPN Active")
            .setContentText("Connected to " + server.getName())
            .setSmallIcon(R.drawable.ic_vpn)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVPN();
    }
} 