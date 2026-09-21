package com.ass1.proxy;

public class ServerInfo {
    public String ip;           // server's ip address
    public int port;            // server's port number
    public String name;         // server's name
    public int zone;            // server's zone number
    public int assignedClients; // number of clients assigned to this server (resets at 18)
    public int waitingListSize; // server's waiting-list size

    public ServerInfo(String ip, int port, String name, int zone) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.zone = zone;
        assignedClients = 0;
        waitingListSize = 0;
    }

    public int getWaitingList() {
        return waitingListSize;
    }

}