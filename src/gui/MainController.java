package gui;

import backend.BackEndManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import util.ServerType;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainController {

    private BackEndManager manager;

    public void setUp() {
        manager = BackEndManager.getSingletonInstance();
        manager.setUIController(this);
        setNodeConnectionInfo(manager.getServerType(),
                manager.getIpAddress(),
                manager.getPort(),
                manager.getConnectingIPAddress(),
                manager.getConnectingPort());
    }

    @FXML private Label nodeConnectionInfo;

    @FXML private VBox messageBox;

    @FXML private VBox dataItemBox;

    public void addMessage(String message) {
        messageBox.getChildren().add(0, new Text(message));
        messageBox.getChildren().add(0, new Separator());
    }

    public void updateDataItems(HashMap<String, Integer> dataItems) {
        //Clear all the previous data items.
        dataItemBox.getChildren().clear();
        //Set the new updated list from the given hashmap.
        for (Map.Entry<String, Integer> entry : dataItems.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            dataItemBox.getChildren().add(new Text("  " + String.format("%-15s %d", key, value)));
            dataItemBox.getChildren().add(new Separator());
        }
    }

    public void updateDataItem(String s, Integer i) {
        //Clear all the previous data items.
        dataItemBox.getChildren().clear();
        dataItemBox.getChildren().add(new Text("  " + s + ":   " + i));
        dataItemBox.getChildren().add(new Separator());
    }


    private void setNodeConnectionInfo(ServerType type, String ipAddress, int port, String connectingIPAddress, int connectingPort) {

        String serverType = "";
        String serverSocketInfo = "";
        String connectingSocketInfo = "";

        switch(type) {
            case SERVER:
                serverType = "Type: Server  ";
                break;
            case CLIENT_SERVER:
                serverType = "Type: Client  ";
                break;
            case CLIENT_TESTER:
                serverType = "Type: Tester  ";
                break;
        }

        //If the node has an open server socket (server and client), format info about the node.
        if(port != -1) {
            serverSocketInfo = "IP Address: " + ipAddress + "  Open Port: " + port;
            //Add a gap if there is socket info since it comes before the connecting info.
            connectingSocketInfo = "  ";
        }

        //If the node is connecting to any other node (client and tester), format info about what node it is connecting to.
        if(connectingPort != -1) {
            connectingSocketInfo += "Connecting IP Address: " + connectingIPAddress + "  Connecting Port: " + connectingPort;
        }

        nodeConnectionInfo.setText(serverType + "  " + serverSocketInfo + connectingSocketInfo);
    }
}
