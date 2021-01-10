package com.example.androidserver;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GetWifiInfo {
    private WifiManager wifiManager;
    public GetWifiInfo(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    public String getLocalIpAddress() throws UnknownHostException {
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }
}
