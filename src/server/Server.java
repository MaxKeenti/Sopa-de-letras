package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private DatagramSocket socket;
    private boolean running;
    private Map<String, GameSession> sessions; // Key: IP:Port
    private ScoreManager scoreManager;

    public Server(int port) throws IOException {
        socket = new DatagramSocket(port);
        sessions = new HashMap<>();
        scoreManager = new ScoreManager();
        System.out.println("Server started on port " + port);
    }

    public void start() {
        running = true;
        byte[] buffer = new byte[1024];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Handle each request in a new thread
                new Thread(() -> handlePacket(packet)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    private void handlePacket(DatagramPacket packet) {
        String data = new String(packet.getData(), 0, packet.getLength()).trim();
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        String clientKey = address.getHostAddress() + ":" + port;

        System.out.println("Received from " + clientKey + ": " + data);

        String response = "";

        // Protocol: COMMAND:PAYLOAD
        String[] parts = data.split(":", 2);
        String command = parts[0];
        String payload = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "START":
                GameSession session = new GameSession();
                sessions.put(clientKey, session);
                response = "BOARD:" + session.getBoardData();
                break;

            case "FOUND":
                GameSession currentSession = sessions.get(clientKey);
                if (currentSession != null && currentSession.isValidWord(payload)) {
                    response = "VALID:" + payload;
                } else {
                    response = "INVALID";
                }
                break;

            case "FINISH":
                // FINISH:Name,Time
                String[] finishParts = payload.split(",");
                if (finishParts.length >= 2) {
                    scoreManager.saveScore(finishParts[0], finishParts[1]);
                    response = "ACK_FINISH";
                    sessions.remove(clientKey);
                } else {
                    response = "ERROR";
                }
                break;

            default:
                response = "UNKNOWN_COMMAND";
        }

        sendResponse(response, address, port);
    }

    private void sendResponse(String msg, InetAddress address, int port) {
        try {
            byte[] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 5000; // Default
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port, using default 5000");
            }
        }
        try {
            new Server(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
