package backend;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientServerThread extends GenericSocketThread implements Runnable {

    public ClientServerThread(Socket connectingSocket) {
        this.connectingSocket = connectingSocket;
        this.sharedResources = BackEndManager.getSingletonInstance();
    }

    public void getNewResourceValues (String message)
    {
        // clear the current mapping

        // reade the message bit by bit
        String curString = "";
        for(char c : message.toCharArray()) {
            // reade everything until a "," is read
            if (c == ',')
            {
                createNewEntry(curString);
                curString = "";
            } else {
                curString = curString + c;
            }
        }
    }

    // Helper function to create a new entry in the hashmap
    private void createNewEntry (String message)
    {
        char[] messageArray= message.toCharArray();

        String resource = "";
        String countString = "";
        int count = 0;

        // traverse the message until you find a ":"
        // store the first part of the message in a string
        int i = 0;
        char c = messageArray[i];
        while (c != ':')
        {
            // store the first characters in the resource string
            resource = resource + c;
            i++;
            c = messageArray[i];
        }
        // skip the ":"
        i++;
        // next take care of the count string
        while (i < messageArray.length)
        {
            countString = countString + messageArray[i];
            i++;
        }

        // convert the count string into an int
        count = Integer.parseInt(countString);

        // create a new value pair in the hashmap
        this.sharedResources.newResourceValue (resource, count);

    }

    public boolean sendCountUpdate ()
    {
        // construct the message with a list of all resources with their respective count
        // after every resource there will be a "," (for simplicity reasons)
        String message = "";

        HashMap<String, Integer> localCounts = sharedResources.getLocalCountsForServer();
        for (Map.Entry<String, Integer> entry: localCounts.entrySet()) {
            message += entry.getKey() + ":" + entry.getValue() + ",";
        }
        return this.sendMessage(message);

    }

    @Override
    public void run() {
        String message;

        while (true) {
            message = this.receiveMessage();
            // check if the server is requesting an update, or if it is sending an update
            if (message.equals("getUpdate")) {
                // the server requested an update
                sharedResources.addMessage( Thread.currentThread().getId() + ": Sending local counts to server.");
                sendCountUpdate();
            } else {
                // the server is sending an update to the client
                sharedResources.addMessage( Thread.currentThread().getId() + ": Received updated view counts from server.");
                getNewResourceValues(message);
                sharedResources.updateDataItems(sharedResources.getTotalResourceCounts());
            }
        }
    }
}
