package com.sopa.web_api.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

@Component
public class GameServer extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private static final int PORT = 9876;
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket socket;
    private boolean running;

    // Store active games or session data if needed. For now, stateless or simple
    // state.
    // In a real scenario, we might map client IP/Port to a Game State.
    // Just for demonstration, let's keep a simple map or just generate on fly.

    // Board size
    private static final int SIZE = 15;

    // Word list
    private static final List<String> WORDS = Arrays.asList("REDES", "SOCKETS", "UDP", "JAVA", "REACT", "DOCKER",
            "SERVIDOR", "CLIENTE", "PROTOCOLO", "API");

    public GameServer() {
        try {
            socket = new DatagramSocket(PORT);
            logger.info("UDP Game Server started on port {}", PORT);
        } catch (Exception e) {
            logger.error("Error starting UDP Game Server", e);
        }
    }

    @Override
    public void run() {
        running = true;
        byte[] buffer = new byte[BUFFER_SIZE];

        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                logger.info("Received from {}:{}: {}", clientAddress, clientPort, received);

                String response = handleRequest(received);
                byte[] responseData = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress,
                        clientPort);
                logger.info("Sending response to {}:{}: {}", clientAddress, clientPort, response);
                socket.send(responsePacket);

            } catch (IOException e) {
                logger.error("Error in Game Server loop", e);
            }
        }
    }

    private String handleRequest(String request) {
        String[] parts = request.split(":");
        String command = parts[0];

        switch (command) {
            case "START_GAME":
                return generateBoard();
            case "VALIDATE_WORD":
                if (parts.length < 2)
                    return "ERROR:Falta Palabra";
                return validateWord(parts[1]);
            default:
                return "ERROR:Comando Desconocido";
        }
    }

    private String generateBoard() {
        char[][] board = new char[SIZE][SIZE];
        Random random = new Random();

        // Fill with random letters
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = (char) ('A' + random.nextInt(26));
            }
        }

        // Place words (Simplified - mostly horizontal/vertical for reliability in demo)
        // Ideally checking for collisions.
        for (String word : WORDS) {
            placeWord(board, word, random);
        }

        // Convert board to String (e.g., JSON or CSV like)
        // Let's use a simple CSV format: ROW1,ROW2,... where ROW is chars
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            sb.append(new String(board[i]));
            if (i < SIZE - 1)
                sb.append(",");
        }
        sb.append(";").append(String.join(",", WORDS)); // Append word list at the end
        return sb.toString();
    }

    private void placeWord(char[][] board, String word, Random random) {
        // Very basic placement: Try 100 times to place a word
        int attempts = 0;
        boolean placed = false;
        while (attempts < 100 && !placed) {
            int dir = random.nextInt(3); // 0: Horizontal, 1: Vertical, 2: Diagonal
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (canPlace(board, word, row, col, dir)) {
                doPlace(board, word, row, col, dir);
                placed = true;
            }
            attempts++;
        }
    }

    private boolean canPlace(char[][] board, String word, int row, int col, int dir) {
        if (dir == 0) { // Horizontal
            if (col + word.length() > SIZE)
                return false;
        } else if (dir == 1) { // Vertical
            if (row + word.length() > SIZE)
                return false;
        } else { // Diagonal
            if (col + word.length() > SIZE || row + word.length() > SIZE)
                return false;
        }
        return true;
    }

    private void doPlace(char[][] board, String word, int row, int col, int dir) {
        for (int i = 0; i < word.length(); i++) {
            if (dir == 0)
                board[row][col + i] = word.charAt(i);
            else if (dir == 1)
                board[row + i][col] = word.charAt(i);
            else
                board[row + i][col + i] = word.charAt(i);
        }
    }

    private String validateWord(String word) {
        return WORDS.contains(word.toUpperCase()) ? "VALID" : "INVALID";
    }

    public void stopServer() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
