package com.ass1.server;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.ass1.proxy.ProxyInterface;

public class ServerSimulator{

    public static void main(String[] args) throws NotBoundException {

        try{
            boolean cachingOn = Boolean.parseBoolean(args[0]);
            boolean useLruEviction = Boolean.parseBoolean(args[1]);
            Registry registry = LocateRegistry.getRegistry(1099); // Må finne ut API til serverproxy 
            ProxyInterface proxy = (ProxyInterface) registry.lookup("proxy");

            int port = 5002;

            for(int i = 1; i < 6; i++){         
                Server server = new Server(i, cachingOn, useLruEviction);
                Registry serverRegistry = LocateRegistry.createRegistry(port);
                ServerInterface serverStub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
                serverRegistry.bind("Server " + i, serverStub);
                proxy.registerNewServer("localhost", port, "Server " + i);
                port++;
            }

            System.out.println("Server is running...");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }
}
