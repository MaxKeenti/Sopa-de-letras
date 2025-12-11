package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreManager {
    private static final String FILE_NAME = "scores.txt";

    public synchronized void saveScore(String playerName, String timeStr) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("[%s] Player: %s - Time: %s", timestamp, playerName, timeStr));
            writer.newLine();
            System.out.println("Score saved for " + playerName);
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }
}
