package statsVisualiser.gui;

import javax.swing.*;
import java.awt.*;

public class StartPage extends JFrame {

    public StartPage() {
        setTitle("Welcome to NutriSci");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Background Panel with Gradient ===
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(76, 161, 175);   // Teal
                Color color2 = new Color(196, 224, 229); // Light Blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // === Title ===
        JLabel titleLabel = new JLabel("NutriSci — “SwEATch to better!”", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // === Developer Credits ===
        JTextArea devArea = new JTextArea(
            "Developed by:\n\n" +
            "• Sachin Kumar\n" +
            "• Yogesh Sharma\n" +
            "• Divyansh Babbar\n" +
            "• Veerman Kalra"
        );
        devArea.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        devArea.setForeground(new Color(33, 33, 33));
        devArea.setOpaque(false);
        devArea.setEditable(false);
        devArea.setFocusable(false);
        devArea.setMargin(new Insets(20, 60, 20, 60));
        mainPanel.add(devArea, BorderLayout.CENTER);

        // === Get Started Button ===
        JButton startButton = new JButton("Get Started ➤");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startButton.setBackground(new Color(34, 102, 102));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(180, 45));
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        startButton.addActionListener(e -> {
            new SplashScreen(); // Launch splash screen
            dispose(); // Close start page
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartPage::new);
    }
}
