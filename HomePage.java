import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HomePage extends JFrame {

    public HomePage() {
        setTitle("Tic Tac Toe");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set a modern dark background
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30)); // Dark background
        GridBagConstraints gbc = new GridBagConstraints();

        // Title label styling
        JLabel titleLabel = new JLabel("Welcome to Tic Tac Toe!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Start Button styling
        JButton startButton = new JButton("Single Player");
        styleButton(startButton, new Color(0, 200, 83)); // Green button

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TicTacToe(); // Replace with appropriate game startup logic
                dispose();
            }
        });

        // Online mode
        JButton onlineButton = new JButton("Multiplayer");
        onlineButton.setFont(new Font("Arial", Font.PLAIN, 18));
        styleButton(onlineButton, new Color(0, 0, 190)); // Blue button

        onlineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new OnlineMode(); // Go to OnlineMode screen
            }
        });

        // Quit Button styling
        JButton quitButton = new JButton("Quit Game");
        styleButton(quitButton, new Color(229, 57, 53)); // Red button

        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Layout configuration
        gbc.insets = new Insets(30, 30, 30, 30);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(startButton, gbc);

        gbc.gridy = 3;
        panel.add(quitButton, gbc);

        gbc.gridy = 2;
        panel.add(onlineButton, gbc);

        add(panel);
        setVisible(true);
    }

    // Method to style buttons
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
