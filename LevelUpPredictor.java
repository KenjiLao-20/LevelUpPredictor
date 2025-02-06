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

        System.out.print("Enter the path to the CSV file: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the Player ID: ");
        String playerId = scanner.nextLine();

        List<PlayerData> playerDataList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (values.length >= 5) {
                    try {
                        String id = values[0].trim();
                        int level = Integer.parseInt(values[2].trim());
                        int score = Integer.parseInt(values[4].trim());
                        playerDataList.add(new PlayerData(id, level, score));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing data in line: " + line);
                    }
                } else {
                    System.err.println("Invalid data format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        int totalScore = 0;
        int sessionCount = 0;
        int currentLevel = 0;

        for (PlayerData data : playerDataList) {
            if (data.playerId.equals(playerId.trim())) {
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
        int sessionsNeeded = calculateSessionsNeeded(currentLevel, averageScore);

        System.out.println("Player ID: " + playerId);
        System.out.println("Current Level: " + currentLevel);
        System.out.println("Average Score per Session: " + averageScore);
        System.out.println("Sessions needed to reach Level 10: " + sessionsNeeded);
    }

    private static int calculateSessionsNeeded(int currentLevel, double averageScore) {
        int targetLevel = 10;
        int scorePerLevel = 1000;
        int totalScoreNeeded = (targetLevel - currentLevel) * scorePerLevel;

        if (averageScore <= 0) {
            return Integer.MAX_VALUE;
        }

        return (int) Math.ceil((double) totalScoreNeeded / averageScore);
    }
}
