package com.example.androidclient;

import java.io.IOException;
import java.net.Socket;

class ClientManagerThread implements Runnable {
    private String serverIP;
    private int serverPort;
    MainActivity mainActivity;

    public ClientManagerThread(MainActivity mainActivity, String serverIP, int serverPort) {
        this.mainActivity = mainActivity;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket;

        try {
            //Connecting to port on the server
            socket = new Socket(serverIP, serverPort);
            //Creating the thread for output stream from client.
            mainActivity.setSocketOutputStreamWriter();

            mainActivity.printClientStatusInUIThread("Connected\n");

            //After successfully connecting to the server, start reading the input stream from the server
            new Thread(new SockerInputStreamReader(mainActivity, socket)).start();
        }
        catch (IOException e) {
            mainActivity.printClientStatusInUIThread("Connection Error: Check if IP address and port are right\n");
            e.printStackTrace();
        }
    }
}