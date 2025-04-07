import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;

public class CreateGame extends JFrame {
    public CreateGame() {
        setTitle("Create Game");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel loadingLabel = new JLabel("Creating game, waiting for player...");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(loadingLabel);

        setVisible(true);

        // Start client connection
        new Thread(() -> {
            OnlineGameClient.startClient("CREATE", null); // Sends request to create game
        }).start();
    }
}
