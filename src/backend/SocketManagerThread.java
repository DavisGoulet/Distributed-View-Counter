package backend;

import util.ServerType;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketManagerThread implements Runnable{

    private ServerSocket serverConnection;
    private ServerType type;

    public SocketManagerThread(ServerType type, ServerSocket serverConnection) {
        this.type = type;
        this.serverConnection = serverConnection;
    }

    /**
     * This thread continuously runs and accepts new client connection to it. When a new client connects,
     * a new thread is spawned to manage it.
     */
    @Override
    public void run() {

        while(true) {
            try {
                Socket client = serverConnection.accept();
                Thread t;
                if(type == ServerType.SERVER) {
                    t = new Thread(new ServerWorkerThread(client));
                } else {
                    t = new Thread(new ClientWorkerThread(client));
                }
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
