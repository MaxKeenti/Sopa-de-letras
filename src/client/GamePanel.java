package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private JLabel[][] gridLabels;
    private char[][] boardData;
    private Point startPoint = null;
    private Point endPoint = null;
    private List<Point> selectedPoints;
    private List<Point> confirmedPoints;
    private Client client;

    public GamePanel(Client client) {
        this.client = client;
        setLayout(new GridLayout(15, 15));
        gridLabels = new JLabel[15][15];
        selectedPoints = new ArrayList<>();
        confirmedPoints = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                JLabel label = new JLabel("", SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setFont(new Font("Monospaced", Font.BOLD, 16));

                final int r = i;
                final int c = j;

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        handleMousePress(r, c);
                    }
                });

                gridLabels[i][j] = label;
                add(label);
            }
        }
    }

    public void setBoard(String boardString, String wordsToFind) { // updated signature
        // Parse board string (15x15 = 225 chars)
        if (boardString.length() < 225)
            return;
        boardData = new char[15][15];
        int idx = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                char c = boardString.charAt(idx++);
                boardData[i][j] = c;
                gridLabels[i][j].setText(String.valueOf(c));
                gridLabels[i][j].setBackground(Color.WHITE);
            }
        }
        confirmedPoints.clear();
        selectedPoints.clear();
        startPoint = null;
        repaint();
    }

    private void handleMousePress(int r, int c) {
        if (boardData == null)
            return;

        if (startPoint == null) {
            startPoint = new Point(r, c);
            gridLabels[r][c].setBackground(Color.YELLOW);
        } else {
            endPoint = new Point(r, c);
            checkSelection();
            startPoint = null;
            endPoint = null;
        }
    }

    private void checkSelection() {
        // Calculate points between start and end (bresenham or simple line step)
        // Must be horizontal, vertical or diagonal
        int r1 = startPoint.x;
        int c1 = startPoint.y;
        int r2 = endPoint.x;
        int c2 = endPoint.y;

        int dr = Integer.compare(r2, r1);
        int dc = Integer.compare(c2, c1);

        // Validate direction (must be line)
        if (dr == 0 && dc == 0)
            return; // same point
        if (dr != 0 && dc != 0 && Math.abs(r2 - r1) != Math.abs(c2 - c1))
            return; // not diagonal

        StringBuilder sb = new StringBuilder();
        List<Point> currentSelection = new ArrayList<>();

        int currR = r1;
        int currC = c1;

        while (true) {
            sb.append(boardData[currR][currC]);
            currentSelection.add(new Point(currR, currC));

            if (currR == r2 && currC == c2)
                break;

            currR += dr;
            currC += dc;
        }

        // Send word to client to validate
        client.validateWord(sb.toString(), currentSelection);
    }

    public void markValid(List<Point> points) {
        for (Point p : points) {
            gridLabels[p.x][p.y].setBackground(Color.GREEN);
            confirmedPoints.add(p);
        }
    }

    public void clearSelection_UI() {
        // Reset backgrounds that are not confirmed
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Point p = new Point(i, j);
                if (!confirmedPoints.contains(p)) { // naive check, Point.equals works? yes
                    gridLabels[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
}
