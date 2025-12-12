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

    // Map to store player scores: Name -> Score
    private final Map<String, Integer> playerScores = new HashMap<>();

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

                // logger.info("Received from {}:{}: {}", clientAddress, clientPort, received);

                String response = handleRequest(received, clientAddress, clientPort);
                byte[] responseData = response.getBytes();

                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress,
                        clientPort);
                // logger.info("Sending response to {}:{}: {}", clientAddress, clientPort,
                // response);
                socket.send(responsePacket);

            } catch (IOException e) {
                logger.error("Error in Game Server loop", e);
            }
        }
    }

    private String handleRequest(String request, InetAddress address, int port) {
        String[] parts = request.split(":");
        String command = parts[0];

        if ("START_GAME".equals(command)) {
            String playerName = (parts.length > 1) ? parts[1] : "Unknown";
            playerScores.put(playerName, 0); // Reset score
            logger.info("Player {} joined from {}:{}", playerName, address, port);
            return generateBoard();
        } else if ("VALIDATE_WORD".equals(command)) {
            if (parts.length < 3)
                return "ERROR:Falta Datos";
            String word = parts[1];
            String playerName = parts[2];

            String result = validateWord(word);

            if ("VALID".equals(result)) {
                int score = playerScores.getOrDefault(playerName, 0) + 1;
                playerScores.put(playerName, score);
                logger.info("Player {} found word {}. Current Score: {}", playerName, word, score);
            } else {
                logger.info("Player {} attempted invalid word {}", playerName, word);
            }

            return result;
        } else if ("END_GAME".equals(command)) {
            if (parts.length < 3)
                return "ERROR:Falta Datos";
            String playerName = parts[1];
            String time = parts[2];

            saveScore(playerName, time);
            logger.info("Game Over for {}. Time: {}", playerName, time);

            return "OK";
        } else {
            return "ERROR:Comando Desconocido";
        }
    }

    private synchronized void saveScore(String name, String time) {
        try (java.io.FileWriter fw = new java.io.FileWriter("scores.txt", true);
                java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
            bw.write(name + "," + time);
            bw.newLine();
        } catch (IOException e) {
            logger.error("Error writing score to file", e);
        }
    }

    private String generateBoard() {
        char[][] board = new char[SIZE][SIZE];
        Random random = new Random();

        // 1. Initialize board with placeholders (0)
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }

        // 2. Place words with collision checking
        Collections.shuffle(WORDS); // Shuffle to randomize positions each game
        for (String word : WORDS) {
            placeWord(board, word, random);
        }

        // 3. Fill remaining spots with random letters
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }

        // Convert board to String (CSV format)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            sb.append(new String(board[i]));
            if (i < SIZE - 1)
                sb.append(",");
        }
        sb.append(";").append(String.join(",", WORDS));
        return sb.toString();
    }

    private void placeWord(char[][] board, String word, Random random) {
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
        if (!placed) {
            logger.warn("Could not place word: {}", word);
        }
    }

    private boolean canPlace(char[][] board, String word, int row, int col, int dir) {
        int len = word.length();

        // Boundary checks
        if (dir == 0) { // Horizontal
            if (col + len > SIZE)
                return false;
        } else if (dir == 1) { // Vertical
            if (row + len > SIZE)
                return false;
        } else { // Diagonal
            if (col + len > SIZE || row + len > SIZE)
                return false;
        }

        // Collision checks
        for (int i = 0; i < len; i++) {
            int r = row;
            int c = col;

            if (dir == 0)
                c += i;
            else if (dir == 1)
                r += i;
            else {
                r += i;
                c += i;
            }

            char current = board[r][c];
            // If cell is not empty AND not matching the character we want to place =>
            // Collision
            if (current != 0 && current != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private void doPlace(char[][] board, String word, int row, int col, int dir) {
        for (int i = 0; i < word.length(); i++) {
            int r = row;
            int c = col;

            if (dir == 0)
                c += i;
            else if (dir == 1)
                r += i;
            else {
                r += i;
                c += i;
            }

            board[r][c] = word.charAt(i);
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
