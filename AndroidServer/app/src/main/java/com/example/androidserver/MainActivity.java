package com.example.androidserver;
import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
// import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 9001;
    String message;
    SocketOutputStreamWriter socketOutputStreamWriter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        try {
            GetWifiInfo wifiInfo = new GetWifiInfo((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
            //Get the IP address of the Wifi
            SERVER_IP = wifiInfo.getLocalIpAddress();

            //Start the server on localhost:9001
            ServerManagerThread serverManager = new ServerManagerThread(this, SERVER_IP, SERVER_PORT);
            new Thread(serverManager).start();

        } catch (UnknownHostException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Listen to the server side message when Clicked "Send"
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    //Flush the message from the server to the client
                    socketOutputStreamWriter.setMessage(message);
                    new Thread(socketOutputStreamWriter).start();
                }
            }
        });
    }

    public void setSocketOutputStreamWriter() {
        socketOutputStreamWriter = new SocketOutputStreamWriter(this);
    }

    public void setOutputStreamForOutputStreamWriter(PrintWriter output){
        socketOutputStreamWriter.setOutputStream(output);
    }

    public void printServerStatusInUIThread(String serverIP, int serverPort, String status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvIP.setText("IP: " + serverIP);
                tvPort.setText("Port: " + String.valueOf(serverPort));
                tvMessages.setText(status);
            }
        });
    }

    public void printMessageInUIThread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessages.append(message);
            }
        });
    }

    public void printMessageFromServerInUIThread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessages.append(message);
                etMessage.setText("");
            }
        });
    }
}