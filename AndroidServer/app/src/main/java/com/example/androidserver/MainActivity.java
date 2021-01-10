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
    ServerSocket serverSocket;
    Thread Thread1 = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 9001;
    String message;

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
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //SERVER_IP =  "192.168.0.110";

        Thread1 = new Thread(new Thread1());
        Thread1.start();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    private PrintWriter global_output_stream;

    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Not connected");
                        tvIP.setText("IP: " + SERVER_IP);
                        tvPort.setText("Port: " + String.valueOf(SERVER_PORT));
                    }
                });

                while (true) {
                    try {
                        socket = serverSocket.accept();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.setText("Connected\n");
                            }
                        });

                        new Thread(new Thread2(socket)).start();
                    }
                    //while (true) {
                            /*output.println();
                            final String message = input.readLine();
                            if (message != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvMessages.append("client:" + message + "\n");
                                    }
                                });
                            }*/
                    //}
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Thread2 implements Runnable {
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;

        public Thread2(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                output = new PrintWriter(socket.getOutputStream());
                global_output_stream = output;
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                output.println("Hi from Server");
                while (true) {
                    try {
                        final String message = input.readLine();
                        if (message != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessages.append("client:" + message + "\n");
                                }
                            });
                        }
                    /*else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            global_output_stream.println(message);
            global_output_stream.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("server: " + message + "\n");
                    etMessage.setText("");
                }
            });
        }
    }
}