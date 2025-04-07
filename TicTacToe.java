import java.awt.*;
// import java.awt.event.*;
import javax.swing.*;

public class TicTacToe {
    int boardWidth = 600;
    int boardHeight = 700;

    JFrame frame = new JFrame("Tic Tac Toe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JButton[][] board = new JButton[3][3];
    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;

    boolean gameOver = false;
    int turns = 0;

    TicTacToe() {
        frame.setSize(boardWidth, boardHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // Title Label
        textLabel.setBackground(new Color(30, 30, 30));
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        textLabel.setOpaque(true);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.NORTH);

        // Board Panel
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5));
        boardPanel.setBackground(new Color(30, 30, 30));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(boardPanel, BorderLayout.CENTER);

        // Reset and Quit Buttons
        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resetButton.setBackground(new Color(66, 165, 245));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resetButton.addActionListener(e -> resetGame());

        JButton quitButton = new JButton("Quit Game");
        quitButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        quitButton.setBackground(new Color(244, 67, 54));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        quitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        quitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        quitButton.addActionListener(e -> System.exit(0));

        bottomPanel.setBackground(new Color(30, 30, 30));
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(resetButton);
        bottomPanel.add(quitButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Tiles
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;
                boardPanel.add(tile);

                tile.setBackground(new Color(50, 50, 50));
                tile.setForeground(Color.WHITE);
                tile.setFont(new Font("Segoe UI", Font.BOLD, 100));
                tile.setFocusable(false);
                tile.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

                tile.addActionListener(e -> {
                    if (gameOver || !tile.getText().isEmpty()) return;

                    tile.setText(currentPlayer);
                    tile.setForeground(currentPlayer.equals(playerX) ? Color.CYAN : Color.RED);
                    turns++;
                    checkWinner();

                    if (!gameOver) {
                        currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                        textLabel.setText(currentPlayer + "'s Turn");
                    }
                });
            }
        }

        frame.setVisible(true);
    }

    void checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].getText().isEmpty() &&
                board[i][0].getText().equals(board[i][1].getText()) &&
                board[i][1].getText().equals(board[i][2].getText())) {
                setWinner(board[i][0], board[i][1], board[i][2]);
                return;
            }

            if (!board[0][i].getText().isEmpty() &&
                board[0][i].getText().equals(board[1][i].getText()) &&
                board[1][i].getText().equals(board[2][i].getText())) {
                setWinner(board[0][i], board[1][i], board[2][i]);
                return;
            }
        }

        if (!board[0][0].getText().isEmpty() &&
            board[0][0].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][2].getText())) {
            setWinner(board[0][0], board[1][1], board[2][2]);
            return;
        }

        if (!board[0][2].getText().isEmpty() &&
            board[0][2].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][0].getText())) {
            setWinner(board[0][2], board[1][1], board[2][0]);
            return;
        }

        if (turns == 9) {
            setTie();
        }
    }

    void setWinner(JButton b1, JButton b2, JButton b3) {
        b1.setBackground(new Color(76, 175, 80));
        b2.setBackground(new Color(76, 175, 80));
        b3.setBackground(new Color(76, 175, 80));
        textLabel.setText(currentPlayer + " Wins!");
        gameOver = true;
    }

    void setTie() {
        textLabel.setText("It's a Tie!");
        for (JButton[] row : board) {
            for (JButton tile : row) {
                tile.setBackground(Color.DARK_GRAY);
                tile.setForeground(Color.ORANGE);
            }
        }
        gameOver = true;
    }

    void resetGame() {
        currentPlayer = playerX;
        turns = 0;
        gameOver = false;
        textLabel.setText("Tic-Tac-Toe");

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = board[r][c];
                tile.setText("");
                tile.setBackground(new Color(50, 50, 50));
                tile.setForeground(Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}
