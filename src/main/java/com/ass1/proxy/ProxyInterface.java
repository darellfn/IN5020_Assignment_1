package com.ass1.proxy;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ProxyInterface extends Remote {

    // for clients

    ArrayList<String> requestServer(int zone) throws RemoteException;

    // for servers

    void registerNewServer(String ip, int port, String name) throws RemoteException;

}