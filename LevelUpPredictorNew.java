import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaApplication2 {

    static class PlayerData {
        String playerId;
        int level;
        int score;

        PlayerData(String playerId, int level, int score) {
            this.playerId = playerId;
            this.level = level;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input for the CSV file
        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();

        // Input for the player ID
        System.out.print("Enter the Player ID: ");
        String playerId = scanner.nextLine();

        List<PlayerData> playerDataList = new ArrayList<>();

        // Read the CSV file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t"); // Assuming tab-separated values
                if (values.length >= 5) {
                    String id = values[0];
                    int level = Integer.parseInt(values[2]);
                    int score = Integer.parseInt(values[4]);
                    playerDataList.add(new PlayerData(id, level, score));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            e.printStackTrace(); // This will print the stack trace for more details
            return;
        }

        // Calculate average score for the specified player
        int totalScore = 0;
        int sessionCount = 0;
        int currentLevel = 0;

        for (PlayerData data : playerDataList) {
            if (data.playerId.equals(playerId)) {
                totalScore += data.score;
                sessionCount++;
                currentLevel = Math.max(currentLevel, data.level);
            }
        }

        if (sessionCount == 0) {
            System.out.println("No data found for Player ID: " + playerId);
            return;
        }

        double averageScore = (double) totalScore / sessionCount;

        // Calculate sessions needed to reach level 10
        int sessionsNeeded = calculateSessionsNeeded(currentLevel, averageScore);

        System.out.println("Player ID: " + playerId);
        System.out.println("Current Level: " + currentLevel);
        System.out.println("Average Score per Session: " + averageScore);
        System.out.println("Sessions needed to reach Level 10: " + sessionsNeeded);
    }

    private static int calculateSessionsNeeded(int currentLevel, double averageScore) {
        int targetLevel = 10;
        int scorePerLevel = 1000; // Assuming 1000 score needed to level up
        int totalScoreNeeded = (targetLevel - currentLevel) * scorePerLevel;

        if (averageScore <= 0) {
            return Integer.MAX_VALUE; // Cannot level up if average score is 0 or less
        }

        return (int) Math.ceil((double) totalScoreNeeded / averageScore);
    }
}
