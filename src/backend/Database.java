package backend;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Database {

    /** Hashmap for resource counts from the last server update. This hashmap is used in both the server and client
     servers to store records. In the server, it stores the actual view counts for each record. */
    public HashMap<String, Integer> resources = new HashMap<String, Integer>();

    /** Hashmap for resources whose counts have changed since the server last updated it. This hashmap is only used in
     the client servers to keep track of how many views have already been accounted for. */
    public HashMap<String, Integer> modifiedResources = new HashMap<String, Integer>();

    private Lock lock = new ReentrantLock();


    /** Returns the total view count for the data item. This includes both the amount of views specified by the server
     and the amount of views this node has processed since that count was provided by the server. */
    public int getResourceCount (String resource) {
        int count;
        lock.lock();
        try {
            modifiedResources.put(resource, modifiedResources.get(resource) + 1);
            count = modifiedResources.get(resource) + resources.get(resource);
        } finally {
            lock.unlock();
        }
        return count;
    }

    /** Updates the resource value. */
    public void newResourceValue (String resource, int newValue) {
        lock.lock();
        try {
            //Update the resource value to the one given by the server.
            resources.put(resource, newValue);
            modifiedResources.putIfAbsent(resource, 0);
        } finally {
            lock.unlock();
        }
    }

    public void updateResource(String resource, int incrementValue) {
        lock.lock();
        try{
            resources.put(resource, resources.get(resource) + incrementValue);
        } finally {
            lock.unlock();
        }
    }

    /** Create a hashmap of local counts to be sent to the server. Add these local counts to the corresponding resource
     *  in the resource hashmap and reset the modofiedResource count back to zero as the count is now accounted for in
     *  the server. */
    public HashMap<String, Integer> getLocalCountsForServer() {

        lock.lock();
        HashMap<String, Integer> modifiedResourceCopy = null;
        try{
            modifiedResourceCopy = getModifiedResourceCopy();

            //Move the local count from the modifiedResource map to the resources map and reset the modifiedResources
            //back to zero.
            for(Map.Entry<String, Integer> entry: modifiedResources.entrySet()) {
                String key = entry.getKey();
                int localCount = entry.getValue();
                resources.put(key, resources.get(key) + localCount);
                modifiedResources.put(key, 0);
            }
        } finally {
            lock.unlock();
        }
        return modifiedResourceCopy;
    }

    /** Update the resource hashmap with a new one from the server that has more accurate results. */
    public void updateResourceCountFromServer(HashMap<String, Integer> serverCounts) {
        lock.lock();
        try {
            resources = serverCounts;
        } finally {
            lock.unlock();
        }
    }

    /** Sets the database up with the newResources map given by the server. This function is used when a client first
     connects to the server node and the server sends the initial counts to the client. */
    public void initializeDB(HashMap<String, Integer> newResources) {

        lock.lock();
        try {
            resources = newResources;

            //Clear any existing resource counts and set the new ones to 0.
            modifiedResources.clear();
            for(Map.Entry<String, Integer> entry: resources.entrySet()) {
                modifiedResources.put(entry.getKey(), 0);
            }
        } finally {
            lock.unlock();
        }
    }

    /** Initialize the database by creating a bunch of temporary records and setting their counts to 0. This is used
     by the server when it is first starting up as to create data items to be used. */
    public void initializeDB(int numRecords) {
        lock.lock();
        try {
            for(int i=0;i < numRecords; i++) {
                resources.put("DataItem"+i, 0);
            }
        } finally {
            lock.unlock();
        }
    }

    /** Clones the resource hashmap and returns it. Used by the UI. */
    public HashMap<String, Integer> getResourcesCopy() {
        lock.lock();
        try {
            HashMap<String, Integer> newMap = new HashMap<>();
            for(Map.Entry entry: resources.entrySet()) {
                newMap.put((String)entry.getKey(), (Integer)entry.getValue());
            }
            return newMap;
        } finally {
            lock.unlock();
        }

    }

    /** Clones the modifiedResource hashmap and returns it. Used by the UI. */
    public HashMap<String, Integer> getModifiedResourceCopy() {
        lock.lock();
        try {
            HashMap<String, Integer> newMap = new HashMap<>();
            for(Map.Entry entry: modifiedResources.entrySet()) {
                newMap.put((String)entry.getKey(), (Integer)entry.getValue());
            }
            return newMap;
        } finally {
            lock.unlock();
        }

    }

    /** Creates a hashmap with the total view counts. Used by the UI. */
    public HashMap<String, Integer> getTotalResourceCounts() {
        lock.lock();
        try {
            HashMap<String, Integer> newMap = new HashMap<>();
            for(Map.Entry entry: resources.entrySet()) {
                String key = (String)entry.getKey();
                int value = (Integer)entry.getValue();
                newMap.put(key, value + modifiedResources.getOrDefault(key, 0));
            }
            return newMap;
        } finally {
            lock.unlock();
        }
    }
}
