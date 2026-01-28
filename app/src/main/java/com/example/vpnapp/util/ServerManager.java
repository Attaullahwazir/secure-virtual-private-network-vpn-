package com.am.vpn.util;

import com.am.vpn.model.VPNServer;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    private static ServerManager instance;
    private List<VPNServer> servers;

    private ServerManager() {
        servers = new ArrayList<>();
        initializeServers();
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    private void initializeServers() {
        // Free Servers - North America
        servers.add(new VPNServer("AM VPN Free US 1", "United States", "New York", "us-free1.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free US 2", "United States", "Los Angeles", "us-free2.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free Canada", "Canada", "Toronto", "ca-free.amvpn.com", 1194, "UDP", false));

        // Premium Servers - North America
        servers.add(new VPNServer("AM VPN Pro NY", "United States", "New York", "us-ny.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro LA", "United States", "Los Angeles", "us-la.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Chicago", "United States", "Chicago", "us-chi.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Miami", "United States", "Miami", "us-mia.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Toronto", "Canada", "Toronto", "ca-tor.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Vancouver", "Canada", "Vancouver", "ca-van.amvpn.com", 1194, "UDP", true));

        // Free Servers - Europe
        servers.add(new VPNServer("AM VPN Free UK", "United Kingdom", "London", "uk-free.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free Germany", "Germany", "Frankfurt", "de-free.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free Netherlands", "Netherlands", "Amsterdam", "nl-free.amvpn.com", 1194, "UDP", false));

        // Premium Servers - Europe
        servers.add(new VPNServer("AM VPN Pro London", "United Kingdom", "London", "uk-lon.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Manchester", "United Kingdom", "Manchester", "uk-man.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Frankfurt", "Germany", "Frankfurt", "de-fra.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Berlin", "Germany", "Berlin", "de-ber.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Paris", "France", "Paris", "fr-par.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Amsterdam", "Netherlands", "Amsterdam", "nl-ams.amvpn.com", 1194, "UDP", true));

        // Free Servers - Asia
        servers.add(new VPNServer("AM VPN Free Japan", "Japan", "Tokyo", "jp-free.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free Singapore", "Singapore", "Singapore", "sg-free.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free India", "India", "Mumbai", "in-free.amvpn.com", 1194, "UDP", false));

        // Premium Servers - Asia
        servers.add(new VPNServer("AM VPN Pro Tokyo", "Japan", "Tokyo", "jp-tok.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Osaka", "Japan", "Osaka", "jp-osa.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Singapore", "Singapore", "Singapore", "sg-pro.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Hong Kong", "Hong Kong", "Hong Kong", "hk-pro.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Seoul", "South Korea", "Seoul", "kr-pro.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Mumbai", "India", "Mumbai", "in-mum.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Delhi", "India", "Delhi", "in-del.amvpn.com", 1194, "UDP", true));

        // Free Servers - Other Regions
        servers.add(new VPNServer("AM VPN Free Australia", "Australia", "Sydney", "au-free.amvpn.com", 1194, "UDP", false));
        servers.add(new VPNServer("AM VPN Free Brazil", "Brazil", "Sao Paulo", "br-free.amvpn.com", 1194, "UDP", false));

        // Premium Servers - Other Regions
        servers.add(new VPNServer("AM VPN Pro Sydney", "Australia", "Sydney", "au-syd.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Melbourne", "Australia", "Melbourne", "au-mel.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro Brazil", "Brazil", "Sao Paulo", "br-pro.amvpn.com", 1194, "UDP", true));
        servers.add(new VPNServer("AM VPN Pro South Africa", "South Africa", "Johannesburg", "za-pro.amvpn.com", 1194, "UDP", true));
    }

    public List<VPNServer> getServers() {
        return servers;
    }

    public List<VPNServer> getFreeServers() {
        List<VPNServer> freeServers = new ArrayList<>();
        for (VPNServer server : servers) {
            if (!server.isPremium()) {
                freeServers.add(server);
            }
        }
        return freeServers;
    }

    public List<VPNServer> getPremiumServers() {
        List<VPNServer> premiumServers = new ArrayList<>();
        for (VPNServer server : servers) {
            if (server.isPremium()) {
                premiumServers.add(server);
            }
        }
        return premiumServers;
    }

    public VPNServer getServerByName(String name) {
        for (VPNServer server : servers) {
            if (server.getName().equals(name)) {
                return server;
            }
        }
        return null;
    }

    public List<String> getServerNames() {
        List<String> names = new ArrayList<>();
        for (VPNServer server : servers) {
            names.add(server.toString());
        }
        return names;
    }
} 