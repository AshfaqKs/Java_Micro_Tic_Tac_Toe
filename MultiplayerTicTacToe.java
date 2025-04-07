import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class MultiplayerTicTacToe extends JFrame {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String playerSymbol;
    String opponentSymbol;
    boolean myTurn = false;

    JButton[][] board = new JButton[3][3];
    JPanel boardPanel = new JPanel();
    JLabel statusLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    public MultiplayerTicTacToe(Socket socket, String playerSymbol) {
        this.socket = socket;
        this.playerSymbol = playerSymbol;
        this.opponentSymbol = playerSymbol.equals("X") ? "O" : "X";

        setupUI();
        setupNetwork();
    }

    private void setupUI() {
        setTitle("Tic Tac Toe - Online Multiplayer");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Label Panel
        statusLabel.setBackground(new Color(30, 30, 30));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setText("Connecting...");
        statusLabel.setOpaque(true);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(statusLabel, BorderLayout.CENTER);
        add(textPanel, BorderLayout.NORTH);

        // Board Panel
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5));
        boardPanel.setBackground(new Color(30, 30, 30));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(boardPanel, BorderLayout.CENTER);

        // Bottom Panel
        bottomPanel.setBackground(new Color(30, 30, 30));
        bottomPanel.setLayout(new FlowLayout());

        JButton leaveButton = new JButton("Leave Match");
        leaveButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        leaveButton.setBackground(new Color(244, 67, 54));
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        leaveButton.addActionListener(e -> {
            out.println("QUIT");
            out.flush();
            JOptionPane.showMessageDialog(this, "You left the match.");
            this.dispose();
            new HomePage();
        });

        bottomPanel.add(leaveButton);
        add(bottomPanel, BorderLayout.SOUTH);

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

                int finalR = r;
                int finalC = c;
                tile.addActionListener(e -> makeMove(finalR, finalC));
            }
        }

        setVisible(true);
    }

    private void setupNetwork() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            if (playerSymbol.equals("X")) {
                myTurn = true;
                statusLabel.setText("Your Turn (X)");
            } else {
                myTurn = false;
                statusLabel.setText("Waiting for opponent...");
            }

            Thread listenerThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        final String receivedMsg = msg;
                        SwingUtilities.invokeLater(() -> handleIncomingMessage(receivedMsg));
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Connection lost.");
                        this.dispose();
                        new HomePage();
                    });
                }
            });

            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleIncomingMessage(String msg) {
        if (msg.startsWith("MOVE")) {
            int r = Character.getNumericValue(msg.charAt(5));
            int c = Character.getNumericValue(msg.charAt(7));
            board[r][c].setText(opponentSymbol);
            board[r][c].setEnabled(false);
            board[r][c].setForeground(opponentSymbol.equals("X") ? Color.CYAN : Color.RED);
            myTurn = true;
            statusLabel.setText("Your Turn (" + playerSymbol + ")");
            checkWinner();
        } else if (msg.equals("QUIT")) {
            JOptionPane.showMessageDialog(this, "Opponent left the match.");
            this.dispose();
            new HomePage();
        } else if (msg.equals("RESTART_REQUEST")) {
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Opponent requested a rematch.\nDo you want to restart the match?",
                        "Restart Request",
                        JOptionPane.YES_NO_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    out.println("RESTART_ACCEPTED");
                    out.flush();
                    restartGame();
                }
            });
        } else if (msg.equals("RESTART_ACCEPTED")) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Opponent accepted restart. Game will reset.");
                restartGame();
            });
        }
    }

    private void makeMove(int r, int c) {
        if (!myTurn || !board[r][c].getText().equals("")) return;

        board[r][c].setText(playerSymbol);
        board[r][c].setEnabled(false);
        board[r][c].setForeground(playerSymbol.equals("X") ? Color.CYAN : Color.RED);
        out.println("MOVE " + r + " " + c);
        myTurn = false;
        statusLabel.setText("Waiting for opponent...");
        checkWinner();
    }

    private void checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (checkLine(board[i][0], board[i][1], board[i][2])) return;
            if (checkLine(board[0][i], board[1][i], board[2][i])) return;
        }

        if (checkLine(board[0][0], board[1][1], board[2][2])) return;
        if (checkLine(board[0][2], board[1][1], board[2][0])) return;

        boolean tie = true;
        for (JButton[] row : board) {
            for (JButton tile : row) {
                if (tile.getText().equals("")) {
                    tie = false;
                    break;
                }
            }
        }

        if (tie) {
            statusLabel.setText("It's a Tie!");
            for (JButton[] row : board) {
                for (JButton tile : row) {
                    tile.setBackground(Color.DARK_GRAY);
                    tile.setForeground(Color.ORANGE);
                }
            }
            showGameOverButtons("It's a Tie!");
        }
    }

    private boolean checkLine(JButton b1, JButton b2, JButton b3) {
        if (!b1.getText().equals("") &&
                b1.getText().equals(b2.getText()) &&
                b2.getText().equals(b3.getText())) {
            b1.setBackground(new Color(76, 175, 80));
            b2.setBackground(new Color(76, 175, 80));
            b3.setBackground(new Color(76, 175, 80));
            statusLabel.setText(b1.getText() + " Wins!");
            showGameOverButtons(b1.getText() + " Wins!");
            return true;
        }
        return false;
    }

    private void showGameOverButtons(String message) {
        for (JButton[] row : board) {
            for (JButton tile : row) {
                tile.setEnabled(false);
            }
        }

        bottomPanel.removeAll();

        JButton restartButton = new JButton("Restart Match");
        JButton menuButton = new JButton("Main Menu");

        restartButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        restartButton.setBackground(new Color(33, 150, 243));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        restartButton.addActionListener(e -> {
            out.println("RESTART_REQUEST");
            out.flush();
            statusLabel.setText("Waiting for opponent to accept restart...");
            restartButton.setEnabled(false);
        });

        menuButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        menuButton.setBackground(new Color(244, 67, 54));
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        menuButton.addActionListener(e -> {
            try {
                if (out != null) out.println("QUIT");
                out.flush();
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                this.dispose();
                new HomePage();
            }
        });

        bottomPanel.add(restartButton);
        bottomPanel.add(menuButton);
        bottomPanel.revalidate();
        bottomPanel.repaint();

        statusLabel.setText(message);
    }

    private void restartGame() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setEnabled(true);
                board[r][c].setBackground(new Color(50, 50, 50));
            }
        }

        myTurn = playerSymbol.equals("X");
        statusLabel.setText(myTurn ? "Your Turn (" + playerSymbol + ")" : "Waiting for opponent...");

        bottomPanel.removeAll();

        JButton leaveButton = new JButton("Leave Match");
        leaveButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        leaveButton.setBackground(new Color(244, 67, 54));
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        leaveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        leaveButton.addActionListener(e -> {
            out.println("QUIT");
            out.flush();
            JOptionPane.showMessageDialog(this, "You left the match.");
            this.dispose();
            new HomePage();
        });

        bottomPanel.add(leaveButton);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
