package backend;

import gui.MainController;
import javafx.application.Platform;
import util.ServerType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

public class BackEndManager {

    //Singleton class representing only one instance of backend every being created.
    private static BackEndManager singletonInstance = null;
    private MainController UIController = null;
    private Database db;

    private String ipAddress = null;
    private int port = -1;
    private String connectingIPAddress = null;
    private int connectingPort = -1;
    private ServerType type;

    //Override the constructor to prevent subclasses.
    private BackEndManager() {}

    /** Get a reference to teh only backend manager. If no instance exists yet, create a new one and use it.
     * @return BackEndManager instance */
    public static BackEndManager getSingletonInstance() {
        if(singletonInstance == null) {
            singletonInstance = new BackEndManager();
        }
        return singletonInstance;
    }

    /** Sets the UI controller so the backend can update it whenever a thread does something with the shared resource.
     * @param controller - Reference to the UI controller. */
    public void setUIController(MainController controller) {
        UIController = controller;
    }

    /** Set up all the threads and sockets needed for the node to function.
     * Should only be called from the OptionController class.
     * This version of the function is used for the server as it has no need to pass in connecting information.
     * @param type - What type of node should be set up.
     * @return String containing the error message if the setup was a failure. Returns null is setup was successful. */
    public String startUp(ServerType type) {
        return startUp(type, null, -1);
    }

    /** Set up all the threads and sockets needed for the node to function.
     *  Should only be called from the OptionController class.
     * @param type - What type of node should be set up.
     * @return String containing the error message if the setup was a failure. Returns null is setup was successful. */
    public String startUp(ServerType type, String connectingIP, int connectingPort) {

        this.db = new Database();
        this.connectingIPAddress = connectingIP;
        this.connectingPort = connectingPort;
        this.type = type;

        if(type == ServerType.SERVER) {
            try {
                //Try to open up a new SocketManagerThread to handle all the client-servers connecting to it.
                ServerSocket sSocket = new ServerSocket(0);
                Thread t = new Thread(new SocketManagerThread(type, sSocket));
                t.start();
                this.ipAddress = InetAddress.getLocalHost().getHostAddress();
                this.port = sSocket.getLocalPort();
            } catch (UnknownHostException e) {
                return "Setup was successful, but local IP address could not be determined.";
            } catch (IOException e) {
                return "Error Occurred when starting up server";
            }

            //Initialize the database and update the UI to show the data items.
            db.initializeDB(25);
            updateDataItems(db.getResourcesCopy());


        } else if (type == ServerType.CLIENT_SERVER) {
            //Try to connect to the main server through the information provided by the user.
            try {
                Socket connectingSocket = new Socket(connectingIP, connectingPort);
                Thread t = new Thread(new ClientServerThread(connectingSocket));
                t.start();
            } catch (IOException e) {
                return "Could not connect to " + connectingIP + ":" + connectingPort;
            }

            //Try to open up a new SocketManagerThread to handle all the client-servers connecting to it.
            ServerSocket sSocket;
            try {
                sSocket = new ServerSocket(0);
                Thread t = new Thread(new SocketManagerThread(type, sSocket));
                t.start();
                this.ipAddress = InetAddress.getLocalHost().getHostAddress();
                this.port = sSocket.getLocalPort();
            } catch (UnknownHostException e) {
                return "Setup was successful, but local IP address could not be determined.";
            } catch (IOException e) {
                return "Error Occurred when starting up server";
            }

        } else {
            //Try to connect to the client server through the information provided by the user.
            try {
                Socket connectingSocket = new Socket(connectingIP, connectingPort);
                Thread t = new Thread(new TesterThread(connectingSocket));
                t.start();
            } catch (IOException e) {
                return "Could not connect to " + connectingIP + ":" + connectingPort;
            }
        }

        return null;
    }

    public int getResourceCount (String resource) {

        int value = db.getResourceCount(resource);
        updateDataItems(db.getTotalResourceCounts());
        return value;
    }

    //public HashMap<String, Integer> getOwnResourceCount() {return db.getResourcesCopy();}

    public void newResourceValue (String resource, int countValue)
    {
        db.newResourceValue(resource, countValue);
    }

    public HashMap<String, Integer> getLocalCountsForServer() {
        return db.getLocalCountsForServer();
    }

    public void updateResourceCountFromServer(HashMap<String, Integer> serverCounts) {
        db.updateResourceCountFromServer(serverCounts);
    }

    public void initializeDB(HashMap<String, Integer> newResources) {
        db.initializeDB(newResources);
    }

    public void updateResource(String resource, int incrementValue) {
        db.updateResource(resource, incrementValue);
    }

    public HashMap<String, Integer> getTotalResourceCounts() {
        return db.getTotalResourceCounts();
    }


    /* These functions are used by the UI to get information about the sockets that are created. The information that
       is returned will be displayed on the UI. */

    public int getConnectingPort() {
        return connectingPort;
    }

    public String getConnectingIPAddress() {
        return connectingIPAddress;
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public ServerType getServerType() {
        return type;
    }

    /*The following are functions for updating the UI. All of these work by creating a task for the FX Application
    thread to run. The syntax Platform.runlater is used to give Runnable objects to this thread. */

    public void updateDataItem(String dataItem, Integer count) {
        Platform.runLater(() -> UIController.updateDataItem(dataItem, count));
    }


    public void updateDataItems(HashMap<String, Integer> map) {
        Platform.runLater(() -> UIController.updateDataItems(map));
    }


    public void addMessage(String message) {
        Platform.runLater(() -> UIController.addMessage(message));
    }
}
