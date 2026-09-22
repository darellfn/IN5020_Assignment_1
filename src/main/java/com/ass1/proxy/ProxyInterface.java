package com.ass1.proxy;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProxyInterface extends Remote {

    // for clients

    String[] requestServer(int zone) throws RemoteException;

    // for servers

    void registerNewServer(String ip, int port, String name) throws RemoteException;

}