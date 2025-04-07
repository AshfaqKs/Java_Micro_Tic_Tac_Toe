import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class OnlineGameClient {
    public static void startClient(String action, String gameId) {
        try {
            // Use server IP address here
            Socket socket = new Socket("192.168.1.3", 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String playerSymbol = "";

            if ("CREATE".equals(action)) {
                out.println("CREATE");

                String createdId = in.readLine(); // Receive Game ID
                if (createdId == null || createdId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Failed to receive Game ID from server.");
                    socket.close();
                    return;
                }

                JOptionPane.showMessageDialog(null, "Your Game ID is: " + createdId + "\nWaiting for another player...");
                playerSymbol = "X"; // Creator is X

                String response = in.readLine(); // Wait for "OK" from server
                if (!"OK".equals(response)) {
                    JOptionPane.showMessageDialog(null, "Error: Failed to start game.");
                    socket.close();
                    return;
                }

            } else if ("JOIN".equals(action)) {
                out.println("JOIN " + gameId);
                String response = in.readLine();

                if ("OK".equals(response)) {
                    JOptionPane.showMessageDialog(null, "Joined Game Successfully. You are O.");
                    playerSymbol = "O"; // Joiner is O
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Game ID or session expired!");
                    socket.close();
                    return;
                }
            }

            // Launch the game board in multiplayer mode
            new MultiplayerTicTacToe(socket, playerSymbol);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect to server.");
        }
    }
}
