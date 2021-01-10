package com.example.androidclient;

import java.io.PrintWriter;

class SocketOutputStreamWriter implements Runnable {
    private String message;
    private MainActivity mainActivity;
    private PrintWriter output;

    SocketOutputStreamWriter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setOutputStream(PrintWriter output){
        this.output = output;
    }

    public void setMessage(String message){
        this.message = message;
    }

    @Override
    public void run() {
        output.println(message);
        output.flush();
        mainActivity.printMessageFromClientInUIThread("client: " + message + "\n");
    }
}