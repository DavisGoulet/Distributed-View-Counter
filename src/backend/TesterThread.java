package backend;

import java.net.Socket;
import java.util.Random;

public class TesterThread extends GenericSocketThread implements Runnable{

    public TesterThread(Socket connectingSocket) {
        this.connectingSocket = connectingSocket;
        this.sharedResources = BackEndManager.getSingletonInstance();
    }

    @Override
    public void run() {

        Random r = new Random();

        //Continuously send requests for one of the data items.
        while(true) {

            //Sleep for two seconds between requests.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Choose a random data item, send a request, wait for a response, then display the results to the UI.
            String name = "DataItem" + r.nextInt(25);
            sharedResources.addMessage("Sending request for: " + name);
            sendMessage(name);
            String[] results = receiveMessage().split(":");
            sharedResources.addMessage("Received response for: " + results[0]);
            sharedResources.updateDataItem(results[0], Integer.parseInt(results[1]));
        }
    }
}
