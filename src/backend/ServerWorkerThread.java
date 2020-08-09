package backend;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Phaser;

public class ServerWorkerThread extends GenericSocketThread implements Runnable {

    public ServerWorkerThread(Socket connectingSocket) {
        this.connectingSocket = connectingSocket;
        this.sharedResources = BackEndManager.getSingletonInstance();
    }

    public boolean sendCountUpdate ()
    {
        // construct the message with a list of all resources with their respective count
        // after every resource there will be a "," (for simplicity reasons)
        String message = "";

        HashMap<String, Integer> localCounts = sharedResources.getTotalResourceCounts();
        for (Map.Entry<String, Integer> entry: localCounts.entrySet()) {
            message += entry.getKey() + ":" + entry.getValue() + ",";
        }
        return this.sendMessage(message);

    }

    public void getNewResourceValues ()
    {
        // clear the current mapping

        String message = this.receiveMessage();
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

        sharedResources.updateResource(resource, count);

    }

    public boolean requestUpdate ()
    {
        return this.sendMessage("getUpdate");
    }

    // create a phaser
    final static Phaser phaser = new Phaser(0);

    @Override
    public void run() {
        //Process the request by communication through the back end manager class.

        sharedResources.addMessage( Thread.currentThread().getId() + ": Connecting to new client");

        phaser.register();


        while (true)
        {

            // send new update to the client
            sharedResources.addMessage( Thread.currentThread().getId() + ": Sending Server Resource Counts.");
            sendCountUpdate();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // first request updates
            sharedResources.addMessage( Thread.currentThread().getId() + ": Requesting local resource counts.");
            requestUpdate();

            // then process the requested update
            getNewResourceValues();

            sharedResources.addMessage( Thread.currentThread().getId() + ": Local resource counts received.");

            //Update the UI
            sharedResources.updateDataItems(sharedResources.getTotalResourceCounts());

            // wait for all the other serverWorkerThreads to finish
            phaser.arriveAndAwaitAdvance();
        }
    }
}
