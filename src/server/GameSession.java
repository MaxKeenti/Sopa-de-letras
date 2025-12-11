package server;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private BoardGenerator boardGenerator;
    private Set<String> foundWords;

    public GameSession() {
        this.boardGenerator = new BoardGenerator();
        this.boardGenerator.generateBoard();
        this.foundWords = new HashSet<>();
    }

    public String getBoardData() {
        return boardGenerator.serializeBoard();
    }

    public boolean isValidWord(String word) {
        // Check if word is in the list of placed words
        for (String w : boardGenerator.getPlacedWords()) {
            if (w.equalsIgnoreCase(word)) {
                foundWords.add(word); // Track found words if we want to enforce all found before finish
                return true;
            }
        }
        return false;
    }
}
