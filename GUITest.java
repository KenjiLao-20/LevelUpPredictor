import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

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
        // Set up the frame
        setTitle("Level-Up Predictor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set a modern look and feel
        UIManager.put("Panel.background", new Color(45, 45, 45));
        UIManager.put("TextField.background", new Color(60, 63, 65));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextArea.background", new Color(60, 63, 65));
        UIManager.put("TextArea.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(70, 130, 180)); // Steel Blue
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Table.background", new Color(60, 63, 65));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.gridColor", Color.LIGHT_GRAY);
        UIManager.put("TableHeader.background", new Color(70, 130, 180)); // Steel Blue
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("ScrollPane.background", new Color(45, 45, 45));

        // Input panel with GridBagLayout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Add some padding

        // Description label
        JLabel descriptionLabel = new JLabel("Predict how many sessions a player needs to reach level 10 based on the player's average score per session");
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        inputPanel.add(descriptionLabel, gbc);

        // Player ID label and field
        gbc.gridwidth = 1; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Player ID:"), gbc);

        playerIdField = new JTextField(10);
        gbc.gridx = 1;
        inputPanel.add(playerIdField, gbc);

        // Upload button
        uploadButton = new JButton("Upload CSV");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        uploadButton.setFocusPainted(false);
        uploadButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(uploadButton, gbc);

        // Result area
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(Color.WHITE, 2 ), "Results", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));

        // Table for displaying data
        String[] columnNames = {"Player ID", "Level", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        dataTable.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(Color.WHITE, 2), "Player Data", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));

        // Add padding around the table
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(resultScrollPane, BorderLayout.SOUTH);

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
        result.append("Total Score: ").append(totalScore).append("\n");
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
