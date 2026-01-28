package com.am.vpn.model;

public class VPNServer {
    private String name;
    private String country;
    private String city;
    private String host;
    private int port;
    private String protocol;
    private boolean isPremium;

    public VPNServer(String name, String country, String city, String host, int port, String protocol, boolean isPremium) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.isPremium = isPremium;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isPremium() {
        return isPremium;
    }
} 