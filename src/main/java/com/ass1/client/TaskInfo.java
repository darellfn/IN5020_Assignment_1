package com.ass1.client;

public class TaskInfo {
    String method;
    long turnaroundTime;
    long executionTime;
    long waitingTime;

    public TaskInfo(String method, long turnaroundTime, long executionTime, long waitingTime) {
        this.method = method;
        this.turnaroundTime = turnaroundTime;
        this.executionTime = executionTime;
        this.waitingTime = waitingTime;
    }

    public String getMethod() {
        return this.method;
    }

    public long getTurnaroundTime() {
        return this.turnaroundTime;
    }

    public long getExecutionTime() {
        return this.executionTime;
    }

    public long getWaitingTime() {
        return this.waitingTime;
    }
}