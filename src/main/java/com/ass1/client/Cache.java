package com.ass1.client;

import java.util.ArrayList;

public class Cache {
    int limit;
    String method;
    ArrayList<CacheItem> cacheStorage;
    
    public Cache(int limit, String method) {
        this.limit = limit;
        this.method = method;
        cacheStorage = new ArrayList<>();
    }

    // class for individual results stored in the cache
    private class CacheItem {
        String query;
        String data;
        long lastAccessed;

        public CacheItem(String query, String data) {
            this.query = query;
            this.data = data;
            this.lastAccessed = System.currentTimeMillis();
        }

        public String getQuery() {
            return query;
        }

        public String getData() {
            return data;
        }

        public long getTimestamp() {
            return lastAccessed;
        }

        public long updateTimestamp() {
            return lastAccessed = System.currentTimeMillis();
        }
    }

    public int getLimit() {
        return limit;
    }

    public String getMethod() {
        return method;
    }

    // changes the update method used (FIFO or OLDEST)
    public boolean setMethod(String method) {
        switch (method) {
            case "1":
            case "FIFO":
                this.method = "FIFO";
                return true;
            case "2":
            case "OLDEST":
                this.method = "OLDEST";
                return true;
            default:    // return false if failed to change method (invalid string given)
                return false;
        }
    }

    // looks for query result in cache
    synchronized public String checkCache(String query) {
        // if a cached query matches the search query, return its data
        for (CacheItem cacheItem : cacheStorage) {
            if (cacheItem.getQuery().equals(query)) {
                if (method.equals("OLDEST")) {   // update the timestamp if using OLDEST method
                    cacheItem.updateTimestamp();
                    if (cacheStorage.remove(cacheItem)) { cacheStorage.add(cacheItem); }    // move the most recently accessed item to the back of the cache list
                } 
                return cacheItem.getData();
            }
        }
        // if query result is not in cache, return null
        return null;
    }


    // add a new query and result to cache
    synchronized public void addToCache(String query, String data) {
        if (checkCache(query) == null) {  // making sure query is not already cached 
            if (cacheStorage.size() < limit) {  // if cache size limit has not been reached, add the query and result
                cacheStorage.add(new CacheItem(query, data));
                return;
            }
            // if query is not in cache, remove another one in cache to make space for the this one
            cacheStorage.remove(0); // remove first (oldest) entry (if using OLDEST method: this has the oldest timestamp because of the sorting done in checkCache)
            cacheStorage.add(new CacheItem(query, data));   // add new entry to the end of the cache list
        }
    }

}
