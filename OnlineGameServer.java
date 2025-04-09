import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class OnlineGameServer {

    private static final int PORT = 5000;
    private static final Map<String, Socket> waitingGames = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = in.readLine();
            if (request == null) return;

            if (request.equals("CREATE")) {
                // Generate Game ID
                String gameId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                waitingGames.put(gameId, socket);
                out.println(gameId); // Send Game ID to creator

                System.out.println("Game created with ID: " + gameId);

                // Wait for second player to join (join will start the session)

            } else if (request.startsWith("JOIN")) {
                String[] parts = request.split(" ");
                if (parts.length != 2) {
                    out.println("ERROR");
                    return;
                }

                String gameId = parts[1].toUpperCase();

                if (waitingGames.containsKey(gameId)) {
                    Socket player1 = waitingGames.remove(gameId);
                    Socket player2 = socket;

                    System.out.println("Game joined with ID: " + gameId);

                    // Notify both clients
                    PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
                    PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

                    out1.println("OK");
                    out2.println("OK");

                    // Start game session between the two clients
                    new Thread(() -> handleGameSession(player1, player2)).start();
                } else {
                    out.println("NOT_FOUND");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleGameSession(Socket player1, Socket player2) {
        try {
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
    
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
    
            Thread t1 = new Thread(() -> forwardMessages(in1, out2));
            Thread t2 = new Thread(() -> forwardMessages(in2, out1));
    
            t1.start();
            t2.start();
    
            t1.join();
            t2.join();
    
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                player1.close();
                player2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void forwardMessages(BufferedReader in, PrintWriter out) {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                System.out.println("Forwarding message: " + msg);
                out.println(msg);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }
}