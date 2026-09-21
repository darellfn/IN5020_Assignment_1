package com.ass1.proxy;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.ass1.server.ServerInterface;

import java.lang.reflect.Array;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;

public class Proxy implements ProxyInterface {
    // HashMap for storing zone numbers and corresponding zone's server's
    // information: < zone number, ServerInfo for that zone's server >
    HashMap<Integer, ServerInfo> servers = new HashMap<>();

    int numZones = 0;   // counter for total number of zones

    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry();
            Proxy proxy = new Proxy();
            ProxyInterface proxyStub = (ProxyInterface) UnicastRemoteObject.exportObject(proxy, 0);
            registry.bind("proxy", proxyStub);
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    // for client to invoke when making a request and needing a server
    public void requestServer(int zone) throws RemoteException {
        ServerInfo server = servers.get(zone);

        // if the zone has a server
        if (server != null) {
            // if the server is not overloaded
            if (server.waitingListSize < 18) {

                // TODO: return this server's info (to client)

                server.assignedClients += 1;    // add 1 to this server's assigned-clients counter
                fetchUpdatedWorkload(zone);     // fetch updated workload data if needed
            }
            else {  // if server is overloaded, try other servers
                int newZone = checkOtherServers(zone);

                // TODO: return this server's info (to client)

                servers.get(newZone).assignedClients += 1;    // add 1 to this server's assigned-clients counter
                fetchUpdatedWorkload(newZone);     // fetch updated workload data if needed
            }
        }
        else {  // if the zone has no server
            requestServer(nextZone(zone));  // move to the next zone (clockwise)
        }
        
    }

    // for a new server to invoke when registering its existence/connection to the proxy
    public void registerNewServer(String ip, int port, String name) throws RemoteException {
        int zone = numZones + 1;  // new zone number is one more than the current largest zone number
        servers.put(zone, new ServerInfo(ip, port, name, zone));    // save all relevant server info in a ServerInfo instance and add it to the hashmap
        numZones += 1;  // update total zones counter
    }


    private int checkOtherServers(int clientZone) {
        ArrayList<ServerInfo> serverList = new ArrayList<>(servers.values());   // make a list of all the servers
        serverList.sort(Comparator.comparing(ServerInfo::getWaitingList));      // sort the list of servers by the length of their waiting-lists (ascending)

        int min = 0, minWaitingList = 0, overloaded = 0;

        for (ServerInfo server : serverList) { 
            if (server.zone != clientZone) {    // ignore the server in the client zone
                // if this is the first server in the list, aka the one with the shortest waiting list
                if (server == serverList.get(0)) {
                    min += 1;    // add 1 to the min counter
                    minWaitingList = server.waitingListSize;    // save the shortest waiting-list length to a variable
                }
                // if this server also has the shortest waiting-list length
                if (server.waitingListSize == minWaitingList) {
                    min += 1;   // add 1 to the min counter
                }
                // if the server is overloaded
                if (server.waitingListSize >= 18) {
                    overloaded += 1;    // add 1 to the overloaded counter
                }
            }
        }
        // if all servers are overloaded (-1 because we ignored the server in the client zone)
        if (overloaded == numZones - 1) {
            return clientZone;  // return client zone
        }
        // if there is more than one shortest waiting-list length
        else if (min > 1) {
            // make a list of only the zone numbers of the servers with the min waiting-list length
            ArrayList<Integer> zoneList = new ArrayList<>();
            for (int i = 0; i < min; i++) {
                zoneList.add(serverList.get(i).zone);
            }
            return findNearestZone(clientZone, zoneList);   // return the nearest zone (clockwise)
        }
        // if there is only one smallest waiting-list length
        else {
            return serverList.get(0).zone;  // return zone of the server with smallest waiting list
        }
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
        
        // checks if update data needs to be fetched and fetches it, all on a separate thread
        Thread fetcher = new Thread() -> {
            if (servers.get(zone).assignedClients >= 18) {  // if this server has been assigned 18 times (or more)
                try {
                    Registry registry = LocateRegistry.getRegistry();
                    ServerInterface server = (ServerInterface) registry.lookup(servers.get(zone).name);
                    servers.get(zone).waitingListSize = server.getWaitingSize();    // fetches the waiting-list length from the server
                    servers.get(zone).assignedClients = 0;  // reset the server's assigned-clients counter to 0
                } catch (RemoteException | NotBoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        fetcher.start();
    }

}