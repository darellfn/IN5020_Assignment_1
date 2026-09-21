package com.ass1.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.AlreadyBoundException;

public class ServerSimulator{

    public static void main(String[] args){

        try{
            for(int i=0; i< 5; i++){
                Registry registry = LocateRegistry.getRegistry(); // Må finne ut API til serverproxy 
                Server server = new Server();
                ServerInterface serverStub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
                registry.bind("server" + i, serverStub);
            }
        }catch(RemoteException | AlreadyBoundException e){
            e.printStackTrace();
        }
    }
}
