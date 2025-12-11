package com.sopa.web_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Service
public class UdpClientService {

    private static final Logger logger = LoggerFactory.getLogger(UdpClientService.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final int TIMEOUT = 5000; // 5 seconds

    public String sendRequest(String message) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
            InetAddress address = InetAddress.getByName(SERVER_HOST);
            byte[] buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SERVER_PORT);
            socket.send(packet);

            byte[] recBuf = new byte[4096]; // Larger buffer for board data
            DatagramPacket responsePacket = new DatagramPacket(recBuf, recBuf.length);
            socket.receive(responsePacket);

            String received = new String(responsePacket.getData(), 0, responsePacket.getLength());
            return received; // Return raw data
        } catch (Exception e) {
            logger.error("Error communicating with Game UDP Server", e);
            return "ERROR";
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
