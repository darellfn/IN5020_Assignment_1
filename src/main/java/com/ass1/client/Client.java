package com.ass1.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import com.ass1.proxy.ProxyInterface;
import com.ass1.server.ServerInterface;

public class Client {
    private final int T = 20;
    private ArrayList<TaskInfo> tasks = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();

    public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException, IOException {
        Client client = new Client();

        boolean clientCache = false;
        boolean serverCache = false;
        boolean naiveServer = false;
        
        // for each potentially added flag in the command line
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-cc":
                case "--client-cache":
                    clientCache = true;
                    System.out.println("Client cache enabled");
                    System.out.println("writing to client cache file");
                    break;
                case "-sc":
                case "--server-cache":
                    serverCache = true;
                    System.out.println("writing to server cache file");
                case "-ns":
                case "--naive-server":
                    naiveServer = true;
                    System.out.println("writing to naive server file");
                default:
                    break;
            }
        }

        client.parseQuery("com/ass1/client/exercise_1_input.txt");
        //client.parseQuery("src\\main\\java\\com\\ass1\\client\\exercise_1_input.txt");

    }

    public void parseQuery(String fileName) throws RemoteException, NotBoundException, InterruptedException, IOException {
        File file = new File(fileName);
        BufferedWriter writer = new BufferedWriter(new FileWriter("naive_server.txt"));

        Registry proxyRegistry = LocateRegistry.getRegistry(1099);
        ProxyInterface proxy = (ProxyInterface) proxyRegistry.lookup("proxy");
    
        try (Scanner scanner = new Scanner(file);) {
            while(scanner.hasNextLine()) {
                String query = scanner.nextLine();
                String[] line = query.split(" ");
                String method = line[0].strip();

                if (method.equals("getPopulationofCountry")) {
                    if (line.length > 3) { //I added this condition because some countries have two names like "United States"
                        String country = line[1] + " " + line[2];
                        String[] zoneLine = line[3].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runPopulationOfCountry(proxy, country, zoneNumber, writer, query, method);   

                    } else if (line.length < 3) { //I added this condition because some query are missing a country e.g. "getPopulationofCountry <missing country here> Zone:3"
                        continue;                     

                    } else {
                        String country = line[1];
                        String[] zoneLine = line[2].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runPopulationOfCountry(proxy, country, zoneNumber, writer, query, method);   
                    }
                    
                } else if (method.equals("getNumberofCities")) {
                    if (line.length > 5) {
                        String country = line[1] + " " + line[2];
                        int threshold = Integer.parseInt(line[3]);
                        String comp = line[4];
                        String[] zoneLine = line[5].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runNumberOfCities(proxy, country, threshold, comp, zoneNumber, writer, query, method);

                    } else if (line.length < 5) { //Missing country on query
                        continue;

                    } else {
                        String country = line[1];
                        int threshold = Integer.parseInt(line[2]);
                        String comp = line[3];
                        String[] zoneLine = line[4].split(":");
                        int zoneNumber = Integer.parseInt(zoneLine[1]);

                        runNumberOfCities(proxy, country, threshold, comp, zoneNumber, writer, query, method);

                    }
                } else if (method.equals("getNumberofCountries")) {
                    int cityCount = Integer.parseInt(line[1]);
                    int threshold = Integer.parseInt(line[2]);
                    String comp = line[3];
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    runNumberOfCountries(proxy, cityCount, threshold, comp, zoneNumber, writer, query, method);

                } else {
                    int cityCount = Integer.parseInt(line[1]);
                    int minPopulation = Integer.parseInt(line[2]);
                    int maxPopulation = Integer.parseInt(line[3]);
                    String[] zoneLine = line[4].split(":");
                    int zoneNumber = Integer.parseInt(zoneLine[1]);

                    runNumberOfCountriesMM(proxy, cityCount, minPopulation, maxPopulation, zoneNumber, writer, query, method);
                }
                Thread.sleep(T);
            }

            for (Thread thread : this.threads) {
                thread.join(); 
                //I am doing this to make sure that we wait for all threads to finish and gather their results before doing 
                // any calculation (avoid making calculation without all of the thread's results)
            }

            calculateStatistics(writer);
            writer.close();

        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    private void runPopulationOfCountry(ProxyInterface proxy, String country, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> { //I am using thread to simulate that a client does not need to wait more than 20 or 50 ms for a earlier client-thread to finish

            try {
                long start = System.currentTimeMillis();
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getPopulationofCountry(country);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            } catch (IOException e) {
                System.out.println("Error");
            }
            
        });
        threads.add(thread); //recurring for all of the four run-methods, I do this to make sure that each thread (client) is finished with their operation
        thread.start();
    }

    private void runNumberOfCities(ProxyInterface proxy, String country, int threshold, String comp, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCities(country, threshold, comp);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            } catch (IOException e) {
                System.out.println("Error");
            }
        });
        threads.add(thread);
        thread.start();
    }

    private void runNumberOfCountries(ProxyInterface proxy, int cityCount, int threshold, String comp, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCountries(cityCount, threshold, comp);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            } catch (IOException e) {
                System.out.println("Error");
            }
        });
        threads.add(thread);
        thread.start();
    }

    private void runNumberOfCountriesMM(ProxyInterface proxy, int cityCount, int minPopulation, int maxPopulation, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCountriesMM(cityCount, minPopulation, maxPopulation);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];
                
                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error");
            } catch (IOException e) {
                System.out.println("Error");
            }
        });
        threads.add(thread);
        thread.start();
    }

    private synchronized void addTaskInfo(String method, long turnaroundTime, long executionTime, long waitingTime) {
        this.tasks.add(new TaskInfo(method, turnaroundTime, executionTime, waitingTime));
    }

    private synchronized void writeToFile(BufferedWriter writer, long turnaroundTime, long serverResult, long waitingTime, long executionTime, String query, String serverName) throws IOException {
        writer.write(serverResult + " " + query + " (turnaround time: " + turnaroundTime + " ms, execution time: " + executionTime + " ms, waiting time: " + waitingTime + " ms, processed by " + serverName + ")");
        writer.newLine();
        System.out.println(serverResult + " " + query + " (turnaround time: " + turnaroundTime + " ms, execution time: " + executionTime + " ms, waiting time: " + waitingTime + " ms, processed by " + serverName + ")");
        //Print for terminal
    }

    private void calculateStatistics(BufferedWriter writer) throws IOException {
        String[] methods = {"getPopulationofCountry", "getNumberofCities", "getNumberofCountries", "getNumberofCountriesMM"};

        for (String method : methods) {
            int count = 0;
            long totalTurnaroundTime = 0; //these three total values are used to find the average time for each method's entries
            long totalExecutionTime = 0;
            long totalWaitingTime = 0;
            long minTurnaroundTime = Long.MAX_VALUE;
            long maxTurnaroundTime = Long.MIN_VALUE;

            for (TaskInfo task : this.tasks) {
                if (method.equals(task.getMethod())) { //find each methods task run
                    totalTurnaroundTime += task.getTurnaroundTime();
                    totalExecutionTime += task.getExecutionTime();
                    totalWaitingTime += task.getWaitingTime();

                    if (task.getTurnaroundTime() < minTurnaroundTime) { //finding the minimum turn-around time
                        minTurnaroundTime = task.getTurnaroundTime();
                    }

                    if (task.getTurnaroundTime() > maxTurnaroundTime) { //finding the maximum turn-around time
                        maxTurnaroundTime = task.getTurnaroundTime();
                    }

                    count++; //increment the total amount for a method's invocation
                }
            }

            if (count > 0) { //Added this check, just in case there is no tasks for a method
                long avgTurnaroundTime = totalTurnaroundTime / count; //finding the averages for these three entries
                long avgExecutionTime = totalExecutionTime / count;
                long avgWaitingTime = totalWaitingTime / count;

                writer.write(method + " avg turn-around time: " + avgTurnaroundTime + " ms, avg execution time: " + 
                avgExecutionTime + " ms, avg waiting time: " + avgWaitingTime + " ms, min turn-around time: " + minTurnaroundTime +
                " ms, max turn-around time: " + maxTurnaroundTime + " ms\n");

                System.out.println(method + " avg turn-around time: " + avgTurnaroundTime + " ms, avg execution time: " + 
                avgExecutionTime + " ms, avg waiting time: " + avgWaitingTime + " ms, min turn-around time: " + minTurnaroundTime +
                " ms, max turn-around time: " + maxTurnaroundTime + " ms\n"); //Print for terminal
            }
        }
    }
}