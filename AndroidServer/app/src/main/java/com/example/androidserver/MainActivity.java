package com.example.androidserver;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
// import android.support.v7.app.AppCompatActivity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.PrintWriter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    String message;
    SocketOutputStreamWriter socketOutputStreamWriter = null;
    MyService myService;
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        //Information Holder, which hold global information
        SingletonInformationHolder informationHolder = SingletonInformationHolder.getInstance();
        informationHolder.setMainActivity(this);

        //Starting the server in the service, so server will be always running in the background
        myService = new MyService();
        intent = new Intent(this, myService.getClass());
        if (!isMyServiceRunning(myService.getClass())) {
            startService(intent);
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

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
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

    public void startDefaultRingtone(){
        //getting systems default ringtone
        MediaPlayer player;
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);

        //setting loop play to true
        //this will make the ringtone continuously playing
        player.setLooping(false);

        //staring the player
        player.start();
    }
}