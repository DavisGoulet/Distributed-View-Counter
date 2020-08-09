package backend;

import java.net.Socket;
import java.util.Random;

public class ClientWorkerThread extends GenericSocketThread implements Runnable {

    public ClientWorkerThread(Socket connectingSocket) {
        this.connectingSocket = connectingSocket;
        this.sharedResources = BackEndManager.getSingletonInstance();
    }

    @Override
    public void run() {

        Random r = new Random();

        String dataItem;
        while((dataItem = receiveMessage()) != null) {
            sendMessage(dataItem + ":" + sharedResources.getResourceCount(dataItem));
        }
    }
}
