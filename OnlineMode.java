import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;

public class OnlineMode extends JFrame {
    public OnlineMode() {
        setTitle("Online Mode");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton createGameBtn = new JButton("Create Game");
        JButton joinGameBtn = new JButton("Join Game");

        createGameBtn.addActionListener(e -> {
            dispose();
            new CreateGame(); // screen to create game
        });

        joinGameBtn.addActionListener(e -> {
            dispose();
            new JoinGame(); // screen to join game
        });

        panel.add(createGameBtn);
        panel.add(joinGameBtn);

        add(panel);
        setVisible(true);
    }
}
