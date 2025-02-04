import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUITest extends JFrame {

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

    private JTextField playerIdField;
    private JTextArea resultArea;
    private JButton uploadButton;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public GUITest() {
        setTitle("Player Data Analyzer");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        playerIdField = new JTextField(15);
        uploadButton = new JButton("Upload CSV");
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        inputPanel.add(new JLabel("Player ID:"));
        inputPanel.add(playerIdField);
        inputPanel.add(uploadButton);

        // Table for displaying data
        String[] columnNames = {"Player ID", "Level", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Button action
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadCSV();
            }
        });
    }

    private void uploadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
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
                            tableModel.addRow(new Object[]{id, level, score});
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing data in line: " + line);
                        }
                    } else {
                        System.err.println("Invalid data format in line: " + line);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String playerId = playerIdField.getText().trim();
            displayResults(playerDataList, playerId);
        }
    }

    private void displayResults(List<PlayerData> playerDataList, String playerId) {
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
            resultArea.setText("No data found for Player ID: " + playerId);
            return;
        }

        double averageScore = (double) totalScore / sessionCount;
        int sessionsNeeded = calculateSessionsNeeded(currentLevel, averageScore);

        StringBuilder result = new StringBuilder();
        result.append("Player ID: ").append(playerId).append("\n");
        result .append("Total Score: ").append(totalScore).append("\n");
        result.append("Session Count: ").append(sessionCount).append("\n");
        result.append("Current Level: ").append(currentLevel).append("\n");
        result.append("Average Score: ").append(String.format("%.2f", averageScore)).append("\n");
        result.append("Sessions Needed to Reach Level 10: ").append(sessionsNeeded).append("\n");

        resultArea.setText(result.toString());
    }

    private int calculateSessionsNeeded(int currentLevel, double averageScore) {
        int targetLevel = 10;
        int sessionsNeeded = 0;

        while (currentLevel < targetLevel) {
            sessionsNeeded++;
            currentLevel++;
            // Assuming a fixed score increase per level for simplicity
            averageScore += 100; // Example score increase per session
        }

        return sessionsNeeded;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUITest frame = new GUITest();
            frame.setVisible(true);
        });
    }
}