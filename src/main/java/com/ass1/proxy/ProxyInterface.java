package com.ass1.proxy;

import java.rmi.Remote;
import java.rmi.RemoteException;
public interface ProxyInterface extends Remote{
    int Add(int num1,int num2) throws RemoteException;
    
}