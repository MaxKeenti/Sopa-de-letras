package client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Client extends JFrame {
    private NetworkClient networkClient;
    private GamePanel gamePanel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private Timer gameTimer;
    private long startTime;
    private String playerName;

    // Validating word selection
    private List<Point> pendingSelection;

    public Client() {
        super("Sopa de Letras - Cliente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        networkClient = new NetworkClient();
        networkClient.setOnMessageReceived(this::onMessage);

        setupUI();
        showConnectionDialog();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        gamePanel = new GamePanel(this);
        add(gamePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Status: Disconnected");
        timerLabel = new JLabel("Time: 00:00");

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(timerLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        gameTimer = new Timer(1000, e -> updateTimer());
    }

    private void showConnectionDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField ipField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");
        JTextField nameField = new JTextField("Player1");

        panel.add(new JLabel("Server IP:"));
        panel.add(ipField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Connect to Server", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            this.playerName = nameField.getText();
            int port = Integer.parseInt(portField.getText());
            networkClient.connect(ipField.getText(), port);
            networkClient.send("START:" + playerName);
            statusLabel.setText("Status: Connecting...");
        }
    }

    private void updateTimer() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long min = elapsed / 60;
        long sec = elapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", min, sec));
    }

    private void onMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            String[] parts = msg.split(":", 2);
            String cmd = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "BOARD":
                    // payload: CHARS#WORD1,WORD2...
                    String[] boardParts = payload.split("#");
                    gamePanel.setBoard(boardParts[0], boardParts.length > 1 ? boardParts[1] : "");
                    statusLabel.setText("Status: Playing");
                    startTime = System.currentTimeMillis();
                    gameTimer.start();
                    break;

                case "VALID":
                    if (pendingSelection != null) {
                        gamePanel.markValid(pendingSelection);
                        statusLabel.setText("Found: " + payload);
                        pendingSelection = null;

                        // Check if all found? Ideally server tells us or we track list locally.
                        // For simplicity, User manually finishes or we track count.
                        // Lets assume we track basic count or wait for user to say "Done"?
                        // Actually, requirement says "Una vez encontradas todas...".
                        // Server should probably track progress or Client.
                        // I'll stick to manual or simple tracking if BoardGenerator sends count.
                    }
                    break;

                case "INVALID":
                    gamePanel.clearSelection_UI();
                    statusLabel.setText("Invalid Word!");
                    pendingSelection = null;
                    break;

                case "ACK_FINISH":
                    statusLabel.setText("Game Over! Score Saved.");
                    gameTimer.stop();
                    break;
            }
        });
    }

    public void validateWord(String word, List<Point> points) {
        this.pendingSelection = points;
        networkClient.send("FOUND:" + word);
    }

    // Add a finish button or auto-finish?
    // Requirement: "Una vez encontradas todas las palabras...".
    // I will add a finish button locally for explicit end or implement logic on
    // valid.

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}
