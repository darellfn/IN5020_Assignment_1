package com.ass1.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote{
    int getWaitingSize() throws RemoteException;

    long[] getPopulationofCountry(String countryName) throws RemoteException;

    long[] getNumberofCities(String countryName, int threshold, String comp) throws RemoteException;

    long[] getNumberofCountries(int cityCount, int threshold, String comp) throws RemoteException;
    
    long[] getNumberofCountriesMM(int cityCount, int minPopulation, int maxPopulation) throws RemoteException;

}
