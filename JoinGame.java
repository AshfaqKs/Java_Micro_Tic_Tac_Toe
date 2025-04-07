import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;

public class JoinGame extends JFrame {
    public JoinGame() {
        setTitle("Join Game");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JLabel label = new JLabel("Enter Game ID:");
        JTextField idField = new JTextField();
        JButton joinBtn = new JButton("Join");

        joinBtn.addActionListener(e -> {
            String gameId = idField.getText();
            if (!gameId.isEmpty()) {
                dispose();
                new Thread(() -> {
                    OnlineGameClient.startClient("JOIN", gameId);
                }).start();
            }
        });

        panel.add(label);
        panel.add(idField);
        panel.add(joinBtn);

        add(panel);
        setVisible(true);
    }
}
