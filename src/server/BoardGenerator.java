package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardGenerator {
    public static final int SIZE = 15;
    private char[][] board;
    private List<String> placedWords;
    private Random random;

    private static final String[] WORD_POOL = {
            "REDES", "SOCKET", "DATAGRAMA", "HILO", "SERVIDOR",
            "CLIENTE", "PROTOCOLO", "INTERNET", "PAQUETE", "JAVA",
            "WIFI", "ROUTER", "SWITCH", "IP", "PUERTO"
    };

    public BoardGenerator() {
        this.board = new char[SIZE][SIZE];
        this.random = new Random();
        this.placedWords = new ArrayList<>();

        // Initialize with empty chars (or placeholder)
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = '\0';
            }
        }
    }

    public void generateBoard() {
        // Pick 5-8 random words
        int numWords = 8;
        List<String> pool = new ArrayList<>();
        for (String w : WORD_POOL)
            pool.add(w);

        for (int i = 0; i < numWords; i++) {
            if (pool.isEmpty())
                break;
            int idx = random.nextInt(pool.size());
            String word = pool.remove(idx);

            if (placeWord(word)) {
                placedWords.add(word);
            }
        }

        // Fill remaining spaces
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }

    private boolean placeWord(String word) {
        int attempts = 0;
        while (attempts < 100) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            int dir = random.nextInt(3); // 0: Horizontal, 1: Vertical, 2: Diagonal

            if (canPlace(word, row, col, dir)) {
                doPlace(word, row, col, dir);
                return true;
            }
            attempts++;
        }
        return false;
    }

    private boolean canPlace(String word, int row, int col, int dir) {
        int len = word.length();
        int dr = 0, dc = 0;

        if (dir == 0)
            dc = 1; // Horizontal
        else if (dir == 1)
            dr = 1; // Vertical
        else {
            dr = 1;
            dc = 1;
        } // Diagonal

        if (row + dr * (len - 1) >= SIZE || col + dc * (len - 1) >= SIZE)
            return false;

        for (int i = 0; i < len; i++) {
            char current = board[row + i * dr][col + i * dc];
            if (current != '\0' && current != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private void doPlace(String word, int row, int col, int dir) {
        int len = word.length();
        int dr = 0, dc = 0;

        if (dir == 0)
            dc = 1;
        else if (dir == 1)
            dr = 1;
        else {
            dr = 1;
            dc = 1;
        }

        for (int i = 0; i < len; i++) {
            board[row + i * dr][col + i * dc] = word.charAt(i);
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public List<String> getPlacedWords() {
        return placedWords;
    }

    // Helper to serialize board for network transmission
    public String serializeBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j]);
            }
        }
        // Append words to find, separated by comma/semicolon
        sb.append("#");
        for (String w : placedWords) {
            sb.append(w).append(",");
        }
        return sb.toString();
    }
}
