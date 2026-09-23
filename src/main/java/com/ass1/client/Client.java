package com.ass1.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.CacheRequest;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import com.ass1.Cache;
import com.ass1.proxy.ProxyInterface;
import com.ass1.server.ServerInterface;

public class Client {
    private static int T = 0;
    private ArrayList<TaskInfo> tasks = new ArrayList<>();
    private ArrayList<Thread> threads = new ArrayList<>();

    private static boolean clientCache = false;
    private static String outputFile = "";
    private static Cache cache = new Cache(45, "1");

    public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException, IOException {
        Client client = new Client();
        
        // handles the command line flags
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                // to enable client caching
                case "-cc":
                case "--client-cache":
                    clientCache = true;
                    System.out.println("Client cache enabled.");
                    break;
                
                // to set the client caching method to FIFO
                case "-fifo":
                    if (clientCache) {
                        cache.setMethod("FIFO");
                        System.out.println("Client caching method set to FIFO.");
                    }
                    else {
                        System.err.println("Error: client caching not enabled. Please enable client caching before choosing");
                        System.exit(1);
                    }
                    break;
                
                // to set the client caching method to OLDEST
                case "-oldest":
                    if (clientCache) {
                        cache.setMethod("OLDEST");
                        System.out.println("Client caching method set to OLDEST.");
                    }
                    else {
                        System.err.println("Error: client caching not enabled. Please enable client caching before choosing");
                        System.exit(1);
                    }
                    break;

                // to set the delay T to 20
                case "-20":
                    if (T == 0) {
                        T = 20;
                        System.out.println("Delay time set to 20 ms");
                    }
                    else {
                        System.err.println("Error: cannot set two delay times.");
                        System.exit(1);
                    }
                    break;

                // to set the delay T to 50
                case "-50":
                    if (T == 0) {
                        T = 50;
                        System.out.println("Delay time set to 50 ms");
                    }
                    else {
                        System.err.println("Error: cannot set two delay times.");
                        System.exit(1);
                    }
                    break;
                    
                // to write the client_cache.txt file
                case "-wcc":
                case "--write-client-cache":
                    if (outputFile == "") { // if not other output file has been set
                        outputFile = "client_cache.txt";
                        System.out.println("Writing client cache file.");
                    }
                    else {  // if output file was already set
                        System.err.println("Error: cannot write to two files at once.");
                        System.exit(1);
                    }
                    break;
                
                // to write the server_cache.txt file
                case "-wsc":
                case "--write-server-cache":
                    if (outputFile == "") { // if not other output file has been set
                        outputFile = "server_cache.txt";
                        System.out.println("Writing server cache file.");
                    }
                    else {  // if output file was already set
                        System.err.println("Error: cannot write to two files at once.");
                        System.exit(1);
                    }
                    break;
                
                // to write the naive_server.txt file
                case "-wns":
                case "--write-naive-server":
                    if (outputFile == "") { // if not other output file has been set
                        outputFile = "naive_server.txt";
                        System.out.println("Writing naive server file.");
                    }
                    else {  // if output file was already set
                        System.err.println("Error: cannot write to two files at once.");
                        System.exit(1);
                    }
                    break;
                
                // anything else is an unrexognized argument
                default:
                    System.err.println("Error: '" + args[i] + "' is an unrecognized argument.");
                    System.exit(1);
                    break;
            }
        }

        if (outputFile == "client_cache.txt" & !clientCache) {
            System.err.println("Error: cannot write to client cache file when client cache is disabled. Please enable client cache.");
            System.exit(1);
        }

        if (T == 0) {
            T = 20;
            System.out.println("Delay time set to 20 ms");
        }

        //client.parseQuery("com/ass1/client/exercise_1_input.txt");
        client.parseQuery("src\\main\\java\\com\\ass1\\client\\exercise_1_input.txt");

    }


    public void parseQuery(String fileName) throws RemoteException, NotBoundException, InterruptedException, IOException {
        File file = new File(fileName);

        if (outputFile == "") { outputFile = "naive_server.txt"; }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

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

                if (clientCache) {
                    System.out.println("getPopulationOfCountry: " + country);
                    String cacheResult = cache.checkCache("getPopulationOfCountry: " + country);   // check the cache for the query
                    if (cacheResult != null) {  // if query is in cache
                        long result = Long.parseLong(cacheResult);
                        System.out.println(result + " - Was in cache!");
                        // TODO: write this to client 
                        writeToFile(writer, 0, result, 0, 0, query, "client cache");
                    }
                    else {
                        System.out.println("not in cache :(");
                    }
                }

                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getPopulationofCountry(country);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                if (clientCache) { cache.addToCache("getPopulationOfCountry: " + country, Long.toString(serverResult)); System.out.println("added to cache -----"); }   // add query to cache (if we are using cache)

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);
                
            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error");
            } catch (IOException e) {
                System.err.println("Error");
            }
            
        });
        threads.add(thread); //recurring for all of the four run-methods, I do this to make sure that each thread (client) is finished with their operation
        thread.start();
    }

    private void runNumberOfCities(ProxyInterface proxy, String country, int threshold, String comp, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                if (clientCache) {
                    String cacheResult = cache.checkCache(query);   // check the cache for the query
                    if (cacheResult != null) {  // if query is in cache
                        long result = Long.parseLong(cacheResult);
                        System.out.println(result + " - Was in cache!");
                        // TODO: write this to client
                        writeToFile(writer, 0, result, 0, 0, query, "client cache");
                    }
                }
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCities(country, threshold, comp);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                if (clientCache) { cache.addToCache(query, Long.toString(serverResult)); }   // add query to cache (if we are using cache)

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error");
            } catch (IOException e) {
                System.err.println("Error");
            }
        });
        threads.add(thread);
        thread.start();
    }

    private void runNumberOfCountries(ProxyInterface proxy, int cityCount, int threshold, String comp, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                if (clientCache) {
                    String cacheResult = cache.checkCache(query);   // check the cache for the query
                    if (cacheResult != null) {  // if query is in cache
                        long result = Long.parseLong(cacheResult);
                        System.out.println(result + " - Was in cache!");
                        // TODO: write this to client
                        writeToFile(writer, 0, result, 0, 0, query, "client cache");
                    }
                }
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCountries(cityCount, threshold, comp);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                if (clientCache) { cache.addToCache(query, Long.toString(serverResult)); }   // add query to cache (if we are using cache)

                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error");
            } catch (IOException e) {
                System.err.println("Error");
            }
        });
        threads.add(thread);
        thread.start();
    }

    private void runNumberOfCountriesMM(ProxyInterface proxy, int cityCount, int minPopulation, int maxPopulation, int zone, BufferedWriter writer, String query, String method) {
        Thread thread = new Thread(() -> {

            try {
                long start = System.currentTimeMillis();

                if (clientCache) {
                    String cacheResult = cache.checkCache(query);   // check the cache for the query
                    if (cacheResult != null) {  // if query is in cache
                        long result = Long.parseLong(cacheResult);
                        System.out.println(result + " - Was in cache!");
                        // TODO: write this to client
                        writeToFile(writer, 0, result, 0, 0, query, "client cache");
                    }
                }
                
                String[] serverInfo = proxy.requestServer(zone);
                int port = Integer.parseInt(serverInfo[1]);
                String serverName = serverInfo[2];
                Registry serverRegistry = LocateRegistry.getRegistry(port);
                ServerInterface serverStub = (ServerInterface) serverRegistry.lookup(serverName);
                
                long[] results = serverStub.getNumberofCountriesMM(cityCount, minPopulation, maxPopulation);
                long serverResult = results[0];
                long waitingTime = results[1];
                long executionTime = results[2];

                if (clientCache) { cache.addToCache(query, Long.toString(serverResult)); }   // add query to cache (if we are using cache)
                
                long end = System.currentTimeMillis();
                long turnaroundTime = end - start;
                addTaskInfo(method, turnaroundTime, executionTime, waitingTime);
                writeToFile(writer, turnaroundTime, serverResult, waitingTime, executionTime, query, serverName);

            } catch (RemoteException | NotBoundException e) {
                System.err.println("Error");
            } catch (IOException e) {
                System.err.println("Error");
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