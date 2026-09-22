package com.ass1.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import com.ass1.proxy.ProxyInterface;
import com.ass1.server.ServerInterface;

public class Client {
    private final int T = 50;

    public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException {
        Client client = new Client();
        client.parseQuery("exercise_1_input.txt");

    }

    public void parseQuery(String fileName) throws RemoteException, NotBoundException, InterruptedException {
        File file = new File(fileName);

        Registry proxyRegistry = LocateRegistry.getRegistry(1099);
        ProxyInterface proxy = (ProxyInterface) proxyRegistry.lookup("proxy");
    
        try (Scanner scanner = new Scanner(file);) {
            while(scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(" ");
                String method = line[0].strip();

                if (method.equals("getPopulationofCountry")) {
                    if (line.length > 3) { //I added this condition because some countries have two names like "United States"
                        String country = line[1] + " " + line[2];
                        String[] zoneLine = line[3].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runPopulationOfCountry(proxy, country, zoneNumber);   

                    } else if (line.length < 3) { //I added this condition because some lines do not include any country e.g. "getPopulationofCountry Zone:3"
                        String[] zoneLine = line[1].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runPopulationOfCountry(proxy, "", zoneNumber);                        

                    } else {
                        String country = line[1];
                        String[] zoneLine = line[2].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runPopulationOfCountry(proxy, country, zoneNumber);   
                    }
                    
                } else if (method.equals("getNumberofCities")) {
                    if (line.length > 5) {
                        String country = line[1] + " " + line[2];
                        int threshold = Integer.parseInt(line[3]);
                        String comp = line[4];
                        String[] zoneLine = line[5].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runNumberOfCities(proxy, country, threshold, comp, zoneNumber);

                    } else if (line.length < 5) {
                        int threshold = Integer.parseInt(line[1]);
                        String comp = line[2];
                        String[] zoneLine = line[3].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);
                        
                        runNumberOfCities(proxy, "", threshold, comp, zoneNumber);

                    } else {
                        String country = line[1];
                        int threshold = Integer.parseInt(line[2]);
                        String comp = line[3];
                        String[] zoneLine = line[4].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runNumberOfCities(proxy, country, threshold, comp, zoneNumber);

                    }
                } else if (method.equals("getNumberofCountries")) {
                    int cityCount = Integer.parseInt(line[1]);
                    int threshold = Integer.parseInt(line[2]);
                    String comp = line[3];
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    runNumberOfCountries(proxy, cityCount, threshold, comp, zoneNumber);

                } else {
                    int cityCount = Integer.parseInt(line[1]);
                    int minPopulation = Integer.parseInt(line[2]);
                    int maxPopulation = Integer.parseInt(line[3]);
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    runNumberOfCountriesMM(proxy, cityCount, minPopulation, maxPopulation, zoneNumber);
                }
                Thread.sleep(T);
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);

        }
    }

    private void runPopulationOfCountry(ProxyInterface proxy, String country, int zone) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                ArrayList<String> serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo.get(1));
                String serverName = serverInfo.get(2);
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                int result = serverStub.getPopulationofCountry(country);

                long end = System.currentTimeMillis();

                System.out.println("Result: " + result + " | Time: " + (end - start) + " ms");

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            }

            
        });

        thread.start();
    }

    private void runNumberOfCities(ProxyInterface proxy, String country, int threshold, String comp, int zone) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                ArrayList<String> serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo.get(1));
                String serverName = serverInfo.get(2);
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                int result = serverStub.getNumberofCities(country, threshold, comp);

                long end = System.currentTimeMillis();

                System.out.println("Result: " + result + " | Time: " + (end - start) + " ms");

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            }

            
        });

        thread.start();

    }

    private void runNumberOfCountries(ProxyInterface proxy, int cityCount, int threshold, String comp, int zone) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                ArrayList<String> serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo.get(1));
                String serverName = serverInfo.get(2);
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                int result = serverStub.getNumberofCountries(cityCount, threshold, comp);

                long end = System.currentTimeMillis();

                System.out.println("Result: " + result + " | Time: " + (end - start) + " ms");

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            }
        });

        thread.start();

    }

    private void runNumberOfCountriesMM(ProxyInterface proxy, int cityCount, int minPopulation, int maxPopulation, int zone) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                ArrayList<String> serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo.get(1));
                String serverName = serverInfo.get(2);
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                int result = serverStub.getNumberofCountriesMM(cityCount, minPopulation, maxPopulation);

                long end = System.currentTimeMillis();

                System.out.println("Result: " + result + " | Time: " + (end - start) + " ms");

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            }
        });

        thread.start();
    }
}
