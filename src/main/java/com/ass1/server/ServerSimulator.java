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
            Registry registry = LocateRegistry.getRegistry(1099); // Må finne ut API til serverproxy 
            ProxyInterface proxy = (ProxyInterface) registry.lookup("proxy");

            int port = 5002;

            for(int i=0; i< 5; i++){
                Server server = new Server(false, false);
                Registry serverRegistry = LocateRegistry.createRegistry(port);
                ServerInterface serverStub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
                serverRegistry.bind("server" + i, serverStub);
                proxy.registerNewServer("localhost", port, "server" + i);
                port++;
            }

            System.out.println("Server is running...");
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }
}
