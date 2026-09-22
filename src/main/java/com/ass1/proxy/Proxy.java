package com.ass1.proxy;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import com.ass1.server.ServerInterface;

public class Proxy implements ProxyInterface {
    // HashMap for storing zone numbers and corresponding zone's server's
    // information: < zone number, ServerInfo for that zone's server >
    HashMap<Integer, ServerInfo> servers = new HashMap<>();

    int numZones = 0;   // counter for total number of zones

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            Proxy proxy = new Proxy();
            ProxyInterface proxyStub = (ProxyInterface) UnicastRemoteObject.exportObject(proxy, 5001);
            registry.rebind("proxy", proxyStub);
            System.out.println("Proxy is running...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // for client to invoke when making a request and needing a server
    public synchronized ArrayList<String> requestServer(int zone) throws RemoteException {
        ServerInfo server = servers.get(zone);

        // if the zone has a server
        if (server != null) {
            // if the server is not overloaded
            if (server.waitingListSize < 18) {
                ArrayList<String> info = new ArrayList<>(); // make a list with the ip address, port, and name of teh server the client hasbeen assigned to
                info.add(server.ip);
                info.add(Integer.toString(server.port));
                info.add(server.name);

                server.assignedClients += 1;    // add 1 to this server's assigned-clients counter
                fetchUpdatedWorkload(zone);     // fetch updated workload data if needed

                return info;    // return the server info to the client
            }
            else {  // if server is overloaded, try other servers
                int newZone = checkOtherServers(zone);
                ArrayList<String> info = new ArrayList<>(); // make a list with the ip address, port, and name of teh server the client hasbeen assigned to
                info.add(servers.get(newZone).ip);
                info.add(Integer.toString(servers.get(newZone).port));
                info.add(servers.get(newZone).name);

                server.assignedClients += 1;    // add 1 to this server's assigned-clients counter
                fetchUpdatedWorkload(newZone);     // fetch updated workload data if needed

                return info;    // return the server info to the client
            }
        }
        else {  // if the zone has no server
            return requestServer(nextZone(zone));  // move to the next zone (clockwise)
            // is here because java doesn't understand a function calling itself apparently (hope its ust that lol)
        }
        
    }

    // for a new server to invoke when registering its existence/connection to the proxy
    public void registerNewServer(String ip, int port, String name) throws RemoteException {
        int zone = numZones + 1;  // new zone number is one more than the current largest zone number
        servers.put(zone, new ServerInfo(ip, port, name, zone));    // save all relevant server info in a ServerInfo instance and add it to the hashmap
        numZones += 1;  // update total zones counter
    }


    private int checkOtherServers(int clientZone) {
        int smallestWaitingList = Integer.MAX_VALUE;
        ArrayList<Integer> bestZones = new ArrayList<>();

        for (ServerInfo server : servers.values()) { 
            if (server.zone == clientZone) {    // ignore the server in the client zone
                // if this is the first server in the list, aka the one with the shortest waiting list
                continue;
            }
            
            if (server.waitingListSize >= 18) {
                continue;
            }

            if (server.waitingListSize < smallestWaitingList) {
                smallestWaitingList = server.waitingListSize;

                bestZones.clear();
                bestZones.add(server.zone);
            } else if (server.waitingListSize == smallestWaitingList) {
                bestZones.add(server.zone);
            }

        }
        if (bestZones.isEmpty()) {
            return clientZone;
        }

        if (bestZones.size() == 1) {
            return bestZones.get(0);
        }

        return findNearestZone(clientZone, bestZones);
    }

    // takes in a zone number and returns the next zone number (clockwise)
    private int nextZone(int zone) {
        // if this zone number is the 'last' one, go back to 1
        if (zone == numZones) {
            return 1;
        }
        else {  // otherwise, return the zone number + 1
            return zone + 1;
        }
    }

    // finds the nearest zone in the list to the start-zone
    private int findNearestZone(int startZone, ArrayList<Integer> zoneList) {
        int nearestZone = startZone;
        int minDistance = numZones;
        for (Integer zone : zoneList) { // for each zone in the zone list, calculate the zone's clockwise distance from the start-zone
            int distance = 0;
            if (startZone < zone) {
                distance = zone - startZone;
            }
            else {
                distance = numZones - (startZone - zone);
            }
            if (distance < minDistance) {   // if the distance is smaller than the spreviously smallest distance, replace the min distance
                minDistance = distance;
                nearestZone = zone;
            }
        }
        return nearestZone;
    }

    private String getIP(int zone) {
        return servers.get(zone).ip;
    }

    private int getPort(int zone) {
        return servers.get(zone).port;
    }

    private String getName(int zone) {
        return servers.get(zone).name;
    }

    // starts a new thread and fetches updated waiting-list data for this server if required
    private void fetchUpdatedWorkload(int zone) throws RemoteException {
        ServerInfo server = servers.get(zone);

        synchronized (server) {
            if (server.assignedClients < 18) {
                return;
            }

            server.assignedClients = 0;
        }
        
        // checks if update data needs to be fetched and fetches it, all on a separate thread
        Thread fetcher = new Thread(() -> {
                try {
                    Registry registry = LocateRegistry.getRegistry(servers.get(zone).port);
                    ServerInterface remoteServer = (ServerInterface) registry.lookup(server.name);
                    int waitingSize = remoteServer.getWaitingSize();  // fetches the waiting-list length from the server
                    servers.get(zone).assignedClients = 0;  // reset the server's assigned-clients counter to 0
                
                    synchronized (server) {
                        server.waitingListSize = waitingSize;
                    }
                } catch (RemoteException | NotBoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            
        });

        fetcher.start();
    }

}