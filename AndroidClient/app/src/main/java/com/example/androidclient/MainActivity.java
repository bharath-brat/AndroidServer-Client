package com.example.androidclient;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    EditText etIP, etPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    String SERVER_IP;
    int SERVER_PORT;
    MainActivity mainActivityInstance = this;
    SocketOutputStreamWriter socketOutputStreamWriter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        Button btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMessages.setText("");
                SERVER_IP = etIP.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());

                ClientManagerThread clientManager = new ClientManagerThread(mainActivityInstance, SERVER_IP, SERVER_PORT);
                new Thread(clientManager).start();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    //Flush the message from the client to the server
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

    public void printClientStatusInUIThread(String status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

    public void printMessageFromClientInUIThread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessages.append(message);
                etMessage.setText("");
            }
        });
    }

    /*private PrintWriter global_output_stream;

    class Thread1 implements Runnable {
        @Override
        public void run() {
            Socket socket;

            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Connected\n");
                    }
                });

                new Thread(new Thread2(socket)).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable {
        Socket socket;
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

                output.println("Hi from Client");
                while (true) {
                    try {
                        final String message = input.readLine();
                        if (message != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessages.append("server: " + message + "\n");
                                }
                            });
                        } *//*else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }*//*
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
                    tvMessages.append("client: " + message + "\n");
                    etMessage.setText("");
                }
            });
        }
    }*/
}