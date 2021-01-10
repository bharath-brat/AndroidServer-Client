package com.example.androidclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class SockerInputStreamReader implements Runnable {
    Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    MainActivity mainActivity;

    public SockerInputStreamReader(MainActivity mainActivity, Socket socket) {
        this.mainActivity = mainActivity;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Sets the output stream, so client can send the message to the server
            mainActivity.setOutputStreamForOutputStreamWriter(output);

            // Initial Broadcast from Client
            output.println("Hi from Client");
            //Waiting indefinitely in the thread, listening to input stream from the server
            while (true) {
                final String message = input.readLine();
                if (message != null) {
                    mainActivity.printMessageInUIThread("server: " + message + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}