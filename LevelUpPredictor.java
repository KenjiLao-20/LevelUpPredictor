import java.util.ArrayList;
import java.util.List;

class Player {
    String playerID;
    String gameID;
    int level;
    int timePlayed; // in minutes
    int score;
    int livesRemaining;
    String characterClass;
    int inGameCurrency;
    int itemsCollected;
    String gameActions;

    public Player(String playerID, String gameID, int level, int timePlayed, int score, int livesRemaining,
                  String characterClass, int inGameCurrency, int itemsCollected, String gameActions) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.level = level;
        this.timePlayed = timePlayed;
        this.score = score;
        this.livesRemaining = livesRemaining;
        this.characterClass = characterClass;
        this.inGameCurrency = inGameCurrency;
        this.itemsCollected = itemsCollected;
        this.gameActions = gameActions;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public int getLevel() {
        return level;
    }

    public int getInGameCurrency() {
        return inGameCurrency;
    }

    public int getScore() {
        return score;
    }

    public int getTimePlayed() {
        return timePlayed;
    }

    public int getItemsCollected() {
        return itemsCollected;
    }
}

public class Main {

    public static void main(String[] args) {
        // Create a list to store players
        List<Player> players = new ArrayList<>();

        // Sample data (Player ID, Game ID, Level, Time Played (mins), Score, Lives Remaining, Character Class, In-Game Currency, Items Collected, Game Actions)
        players.add(new Player("P006", "G001", 3, 45, 1200, 3, "Warrior", 500, 12, "Jump, Attack, Collect"));
        players.add(new Player("P002", "G001", 5, 30, 800, 2, "Mage", 250, 8, "Cast Spell, Jump, Attack"));
        players.add(new Player("P007", "G002", 2, 20, 600, 1, "Archer", 150, 5, "Shoot, Jump, Collect"));
        players.add(new Player("P004", "G001", 4, 50, 1000, 0, "Rogue", 400, 10, "Stealth, Attack, Jump"));
        players.add(new Player("P003", "G003", 1, 10, 300, 4, "Warrior", 100, 2, "Jump, Attack"));
        // Add more players as needed...

        // Specify the player to predict level up
        String playerID = "P004"; // Example player ID
        String gameID = "G001"; // Game ID for the player

        // Call the function to predict level up
        predictLevelUp(players, playerID, gameID);
    }

    public static void predictLevelUp(List<Player> players, String playerID, String gameID) {
        for (Player player : players) {
            if (player.getPlayerID().equals(playerID) && player.getGameID().equals(gameID)) {
                int currentLevel = player.getLevel();
                int score = player.getScore();

                // Check if the player's level is valid for prediction
                if (currentLevel < 1 || currentLevel > 9) {
                    System.out.println("Player with level " + currentLevel + " is invalid. Level must be between 1 and 9.");
                    return;
                }

                // Calculate the total score needed to reach level 10
                int scoreToLevel10 = 10000; // Example score needed to reach level 10
                int totalScoreNeeded = scoreToLevel10 - (currentLevel * (scoreToLevel10 / 10));

                if (totalScoreNeeded <= 0) {
                    System.out.println("Player with level " + currentLevel + " is already at or above level 10!");
                    return;
                }

                // Calculate the number of sessions needed
                int sessionsNeeded = (int) Math.ceil((double) totalScoreNeeded / score);
                System.out.println("Player with ID " + playerID + " and level " + currentLevel + " needs approximately " + sessionsNeeded + " sessions to reach level 10.");
                return; // Exit after finding the player
            }
        }
        System.out.println("Player not found.");
    }
}
