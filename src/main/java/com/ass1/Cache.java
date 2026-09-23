package com.ass1;

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
            case "FIFO":
                this.method = "FIFO";
                return true;
            case "OLDEST":
                this.method = "OLDEST";
                return true;
            default:    // return false if failed to change method (invalid string given)
                return false;
        }
    }

    // looks for query result in cache
    public String checkCache(String query, String data) {
        // if a cached query matches the search query, return its data
        for (CacheItem cacheItem : cacheStorage) {
            if (cacheItem.getQuery() == query) {
                if (method == "OLDEST") {   // update the timestamp if using OLDEST method
                    cacheItem.updateTimestamp();
                    // TODO: unsure about this line below: (should i move stuff around in the cache?)
                    if (cacheStorage.remove(cacheItem)) { cacheStorage.add(cacheItem); }    // move the most recently accessed item to the back of the cache list
                } 
                return cacheItem.getData();
            }
        }
        // if query result is not in cache, return null
        return null;
    }


    // add a new query and result to cache
    public void addToCache(String query, String data) {
        if (checkCache(query, data) == null) {  // making sure query is not already cached 
            if (cacheStorage.size() < limit) {  // if cache size limit has not been reached, add the query and result
                cacheStorage.add(new CacheItem(query, data));
                return;
            }
            if (method == "FIFO") {
                fifo(query, data); 
            }
            else {  // if method == "OLDEST"
                oldest(query, data);
            }
        }
    }

    // method 1: FIFO
    public void fifo(String query, String data) {
        cacheStorage.remove(cacheStorage.size() - 1);   // remove first (oldest) entry
        cacheStorage.add(new CacheItem(query, data));   // add new entry to the end of the cache list
    }

    // method 2: OLDEST
    public void oldest(String query, String data) {
        // TODO: use this is we are not sorting the list in checkCache:
        long minTimestamp = System.currentTimeMillis();
        CacheItem oldestCacheItem = null;
        // iterate over cache items to find the one with oldest timestamp
        for (CacheItem cacheItem : cacheStorage) {
            if (cacheItem.getTimestamp() < minTimestamp) {
                minTimestamp = cacheItem.getTimestamp();
                oldestCacheItem = cacheItem;
            }
        }
        cacheStorage.remove(oldestCacheItem);   // remove entry with oldest timestamp
        cacheStorage.add(new CacheItem(query, data));   // add new entry to the end of the cache list

        // TODO: literally the same as fifo method if we sort the list in checkCache
        cacheStorage.remove(cacheStorage.size() - 1);   // remove first entry (has the oldest timestamp because of the sorting done in checkCache)
        cacheStorage.add(new CacheItem(query, data));   // add new entry to the end of the cache list
    }

}
