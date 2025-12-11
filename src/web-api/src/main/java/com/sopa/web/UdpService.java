package com.sopa.web;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.*;

@Service
public class UdpService {
    private DatagramSocket socket;
    // Hardcoded for now or configurable via application.properties
    private String serverIp = "game-server";
    private int serverPort = 5000;

    public UdpService() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000); // 3 sec timeout
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setServerAddress(String ip, int port) {
        this.serverIp = ip;
        this.serverPort = port;
    }

    public String sendAndReceive(String message) {
        try {
            InetAddress address = InetAddress.getByName(serverIp);
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, serverPort);

            socket.send(packet);

            byte[] buffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (IOException e) {
            return "ERROR:" + e.getMessage();
        }
    }
}
