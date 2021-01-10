package com.example.androidserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerManagerThread implements Runnable {
    private String serverIP;
    private int serverPort; // 9001
    private InetAddress serverIPAddress;  //"192.168.0.101";
    private int backlog; //Max number of connection
    private ServerSocket serverSocket;
    MainActivity mainActivity;

    public ServerManagerThread(MainActivity mainActivity, String serverIP, int serverPort) throws UnknownHostException {
        this.mainActivity = mainActivity;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.serverIPAddress = InetAddress.getByName(serverIP);
        this.backlog = 100;
    }

    @Override
    public void run() {
        Socket socket;
        try {
            //Creates a new server socket
            serverSocket = new ServerSocket(serverPort, backlog, serverIPAddress);
            //Creating the thread for output stream from server.
            mainActivity.setSocketOutputStreamWriter();

            mainActivity.printServerStatusInUIThread(serverIP, serverPort, "Listening: No Connection");

            while (true) {
                //Waiting for any connection on the socket
                socket = serverSocket.accept();
                mainActivity.printServerStatusInUIThread(serverIP, serverPort, "Connected\n");

                //After successfully connecting to the client, start reading the input stream from the server
                new Thread(new SocketInputStreamReader(mainActivity, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}