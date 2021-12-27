package com.meow.smallquilt.ServerMonitor;

public class MonitorConfig {

    private String name;
    private String host;
    private int port;
    private int interval;
    private String closeCommand;
    private String startCommand;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getCloseCommand() {
        return closeCommand;
    }

    public void setCloseCommand(String closeCommand) {
        this.closeCommand = closeCommand;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }
}
