package com.ass1.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote{
    int Add(int num1,int num2) throws RemoteException;

    int getPopulationofCountry(String countryName) throws RemoteException;

    int getNumberofCities(String countryName, int threshold, String comp) throws RemoteException;

    int getNumberofCountries(int cityCount, int threshold, String comp) throws RemoteException;
    
    int getNumberofCountriesMM(int cityCount, int minPopulation, int maxPopulation) throws RemoteException;

}
