package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

public class NetworkClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private Consumer<String> onMessageReceived;
    private boolean running;

    public NetworkClient() {
        try {
            socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect(String ip, int port) {
        try {
            this.serverAddress = InetAddress.getByName(ip);
            this.serverPort = port;
            this.running = true;

            // Start listening thread
            new Thread(this::listen).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    public void send(String msg) {
        if (serverAddress == null)
            return;
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        byte[] buffer = new byte[4096]; // larger buffer for board data
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                if (onMessageReceived != null) {
                    onMessageReceived.accept(msg);
                }
            } catch (IOException e) {
                if (running)
                    e.printStackTrace();
            }
        }
    }

    public void close() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
