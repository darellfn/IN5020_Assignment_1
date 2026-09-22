package com.ass1.server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;


public class Server implements ServerInterface{
    //Dataset of all cities 
    List <City> cities = new ArrayList<>(); 
    //String filename = "exercise_1_dataset.csv";
    String filename = "src\\main\\java\\com\\ass1\\server\\exercise_1_dataset.csv";


    //Queue of waiting tasks 
    private final BlockingQueue<FutureTask<long[]>> waitingList = new LinkedBlockingQueue<>();
  

    public Server(){
        readFile(filename);
        
        // One worker thread picking up tasks to execute in FIFO order 
        Thread worker = new Thread(() -> {
            while(true){
                try{
                    FutureTask<long[]> task = waitingList.take(); //takes a task from waiting list 
                    logtoFile();
                    task.run(); //executes the task 
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        
        worker.start();
    }

    public int getWaitingSize(){
        return waitingList.size();
    }
    

    //Reads the datafile and puts the values into cities list 
    private void readFile(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = br.readLine();
            while((line = br.readLine()) != null){
                String[] values = line.split(";");
                cities.add(new City( 
                    Integer.parseInt(values[0]),
                    values[3],
                    Integer.parseInt(values[4])));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    // Each row from the datafile will become one city object with the relevant info for the queries. 
    public class City {
        private final int geonameId;
        private final String countryName;
        private final int population;
    
        private City(int geonameId, String countryName,int population) {
            this.geonameId = geonameId;
            this.countryName = countryName;
            this.population = population;
        }
        public int getGeonameId() {return geonameId;}
        public int getPopulation() { return population;}
        public String getCountryName() { return countryName;}
    }


    public long[] getPopulationofCountry(String countryName)throws RemoteException{
        return submit(() -> cities.stream() 
                           .filter( c -> c.getCountryName().equalsIgnoreCase(countryName)) //Only keeps city objects with given countryName
                           .mapToInt(City::getPopulation) //Only keeps population numbers 
                           .sum()); //Sums up population each city in given country 
    }

    public long[] getNumberofCities(String countryName, int threshold, String comp) throws RemoteException{
        return submit(() ->  (int) cities.stream()
                                      .filter(c -> c.getCountryName().equalsIgnoreCase(countryName)) //Only keeps city objects with given countryName
                                      .filter(c -> comp.equals("min")? c.getPopulation() >= threshold : c.getPopulation() <= threshold) //Only keeps city objects within the population size threshold 
                                      .count()); // Counts how many objects that satisfy the conditions above 


    }

    public long[] getNumberofCountries(int cityCount, int threshold, String comp) throws RemoteException {
         return submit(() -> (int) cities.stream()
                     .filter(c -> comp.equals("min") ? c.getPopulation() >= threshold : c.getPopulation() <= threshold) //Only keeps city objects within the population threshold 
                     .collect(Collectors.groupingBy(City::getCountryName, Collectors.counting())) //Maps each country to how many cities they have that satisfy the population threshold
                     .values() //Get the cityCount for each country 
                     .stream()
                     .filter(count -> count >= cityCount) //only keep the countries that have at least cityCount cities 
                     .count()); //Count how many countries satisfy the codition above 
    }

 

    public long[] getNumberofCountriesMM(int cityCount, int minPopulation, int maxPopulation)throws RemoteException{
        return submit( () -> (int) cities.stream()
                                         .filter(c -> c.getPopulation() >= minPopulation && c.getPopulation() <= maxPopulation) //Only keeps city objects within the population threshold
                                         .collect(Collectors.groupingBy(City::getCountryName, Collectors.counting())) //Maps each country to how many cities they have that satisfy the population threshold
                                         .values() // Get the cityCount for each country 
                                         .stream()
                                         .filter(count ->  count >= cityCount) //only keep the countries that have at least cityCount cities 
                                         .count()); //Count how many countries satisfy the codition above 
    }
    
    // Used to write to the log keeping track of queue info 
    private void logtoFile(){
        try  (FileWriter fw = new FileWriter("server_log.csv", true)){
            fw.write(waitingList.size() + " " + System.currentTimeMillis() + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private long[] submit(Callable<Integer> task) throws RemoteException {
    long enqueueTime = System.currentTimeMillis();

        FutureTask<long[]> future = new FutureTask<>(() -> {
            long dequeueTime = System.currentTimeMillis();
            long waitingTime = dequeueTime - enqueueTime;

            long execStart = System.currentTimeMillis();
            int result = task.call();
            long execEnd = System.currentTimeMillis();
            long executionTime = execEnd - execStart;

            return new long[] { result, waitingTime, executionTime };
        });

        try {
            Thread.sleep(80); // simulate network latency
            waitingList.add(future);
            logtoFile();
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Interrupted", e);
        } catch (ExecutionException e) {
            throw new RemoteException("Task failed", e);
        }
    }
    



    

   


    public static void main(String[] args){
        try {
            Server server = new Server();
           
            System.out.println("Number of Countries: " + server.getPopulationofCountry("Norway")[0]);
            System.out.println("Number of Cities: " + server.getNumberofCities("Norway",100000,"min")[0]);
            System.out.println("Number of Countries: " + server.getNumberofCountries(2,5000000,"min")[0]);
            System.out.println("Number of Countries: " + server.getNumberofCountriesMM(30,100000,800000)[0]);

        } catch (RemoteException e ) {
            e.printStackTrace();
        }

    }
}

