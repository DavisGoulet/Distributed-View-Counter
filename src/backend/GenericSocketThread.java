package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GenericSocketThread {

    Socket connectingSocket;
    BackEndManager sharedResources;

    public boolean sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(connectingSocket.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + connectingSocket.getLocalAddress());
            System.exit(1);
            return false;
        }
        return true;
    }

    public String receiveMessage() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connectingSocket.getInputStream()));
            return in.readLine();

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + connectingSocket.getLocalAddress());
            System.exit(1);
            return null;
        }
    }
}
