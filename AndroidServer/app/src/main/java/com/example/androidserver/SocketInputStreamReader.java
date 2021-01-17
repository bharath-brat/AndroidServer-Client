package com.example.androidserver;

import android.media.MediaPlayer;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketInputStreamReader implements Runnable {
    MainActivity mainActivity;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public SocketInputStreamReader(MainActivity mainActivity, Socket socket) {
        this.mainActivity = mainActivity;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            //Sets the output stream, so server can broadcast the message to the client
            mainActivity.setOutputStreamForOutputStreamWriter(output);

            // Initial Broadcast from server
            output.println("Hi from Server");
            //Waiting indefinitely in the thread, listening to input stream from the client
            while (true) {
                final String message = input.readLine();
                if (message != null) {
                    mainActivity.printMessageInUIThread("client:" + message + "\n");
                    mainActivity.startDefaultRingtone();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}