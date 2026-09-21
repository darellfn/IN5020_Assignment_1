package com.ass1.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

// import com.ass1.proxy.ProxyInterface;
// import com.ass1.server.ServerInterface;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        parseQuery("exercise_1_input.txt");

        System.out.println("IT WORKS YOOHOO!!!");
    }

    public static void parseQuery(String fileName) throws RemoteException, NotBoundException {
        File file = new File(fileName);

        // Registry proxyRegistry = LocateRegistry.getRegistry("localhost", 1099);
        // ProxyInterface proxy = (ProxyInterface) proxyRegistry.lookup("Proxy");
    
        try (Scanner scanner = new Scanner(file);) {
            while(scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                String method = line[0].strip();

                if (method.equals("getPopulationofCountry")) {
                    if (line.length > 3) { //I added this condition because some countries have two names like "United States"
                        String country = line[1] + " " + line[2];
                        String[] zoneLine = line[3].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);


                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getPopulationofCountry(country));
                        //System.out.println("Method: " + method + " country: " + country + " Zone: " + zoneNumber);

                    } else if (line.length < 3) { //I added this condition because some lines do not include any country e.g. "getPopulationofCountry Zone:3"
                        String[] zoneLine = line[1].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getPopulationofCountry(""));
                        
                        //System.out.println("Method: " + method + " Zone: " + zoneNumber);

                    } else {
                        String country = line[1];
                        String[] zoneLine = line[2].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getPopulationofCountry(country));
                        
                        //System.out.println("Method: " + method + " country: " + country + " Zone: " + zoneNumber);
                    }
                    
                } else if (method.equals("getNumberofCities")) {
                    if (line.length > 5) {
                        String country = line[1] + " " + line[2];
                        int threshold = Integer.parseInt(line[3]);
                        String comp = line[4];
                        String[] zoneLine = line[5].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getNumberofCities(country, threshold, comp));

                        //System.out.println("Method: " + method + " country: " + country + " Threshold: " + threshold + " Comp: " + comp + " Zone: " + zoneNumber);

                    } else if (line.length < 5) {
                        int threshold = Integer.parseInt(line[1]);
                        String comp = line[2];
                        String[] zoneLine = line[3].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getNumberofCities("", threshold, comp));

                        //System.out.println("Method: " + method + " Threshold: " + threshold + " Comp: " + comp + " Zone: " + zoneNumber);

                    } else {
                        String country = line[1];
                        int threshold = Integer.parseInt(line[2]);
                        String comp = line[3];
                        String[] zoneLine = line[4].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        // String address = proxy.getAddress(zoneNumber);
                        // int port = proxy.getPort(zoneNumber);
                        // System.out.println(port);
                        // System.out.println(address);
                        // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                        // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                        // System.out.println(serverStub.getNumberofCities(country, threshold, comp));

                        //System.out.println("Method: " + method + " country: " + country + " Threshold: " + threshold + " Comp: " + comp + " Zone: " + zoneNumber);
                    }
                } else if (method.equals("getNumberofCountries")) {
                    int cityCount = Integer.parseInt(line[1]);
                    int threshold = Integer.parseInt(line[2]);
                    String comp = line[3];
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    // String address = proxy.getAddress(zoneNumber);
                    // int port = proxy.getPort(zoneNumber);
                    // System.out.println(port);
                    // System.out.println(address);
                    // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                    // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                    // System.out.println(serverStub.getNumberofCountries(cityCount, threshold, comp));

                    //System.out.println("Method: " + method + " City Count: " + cityCount + " Threshold: " + threshold + " Comp: " + comp + " Zone: " + zoneNumber);
                } else {
                    int cityCount = Integer.parseInt(line[1]);
                    int minPopulation = Integer.parseInt(line[2]);
                    int maxPopulation = Integer.parseInt(line[3]);
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    // String address = proxy.getAddress(zoneNumber);
                    // int port = proxy.getPort(zoneNumber);
                    // System.out.println(port);
                    // System.out.println(address);
                    // Registry serverRegistry = LocateRegistry.getRegistry(address, port);
                    // ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(proxy.getName(zoneNumber));
                    // System.out.println(serverStub.getNumberofCountriesMM(cityCount, minPopulation, maxPopulation));
                    
                    //System.out.println("Method: " + method + " City Count: " + cityCount + " Min population: " + minPopulation + " Max population: " + maxPopulation + " Zone: " + zoneNumber);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
}
