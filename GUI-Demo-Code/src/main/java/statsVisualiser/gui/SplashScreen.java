package statsVisualiser.gui;

import DAO.UserProfileDao;
import Models.UserProfile;
import Models.Session;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SplashScreen extends JFrame {
    private final UserProfileDao dao = new UserProfileDao();
    private final JPanel userListPanel = new JPanel();

    public SplashScreen() {
        setTitle("NutriSci - Select Profile");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Background Panel with Gradient ===
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(76, 161, 175),
                        0, getHeight(), new Color(196, 224, 229));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // === Title ===
        JLabel title = new JLabel("Select Your Profile", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        backgroundPanel.add(title, BorderLayout.NORTH);

        // === User List Panel ===
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(userListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // === Create Profile Button ===
        JButton createBtn = new JButton("Create Profile");
        styleMainButton(createBtn);
        createBtn.addActionListener(e ->
                new CreateProfileDialog(this, dao, this::refresh).setVisible(true)
        );

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        bottomPanel.add(createBtn);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        refresh();
        setVisible(true);
    }

    private void refresh() {
        userListPanel.removeAll();
        List<UserProfile> users = dao.getAllUserProfiles();

        for (UserProfile user : users) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setOpaque(false);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

            JButton profileBtn = new JButton(user.getName());
            styleProfileButton(profileBtn);
            profileBtn.addActionListener(e -> {
                Session.login(user.getUserId());
                new Dashboard(this, user).setVisible(true);
                this.dispose();
            });

            JButton editBtn = new JButton("Edit");
            styleEditButton(editBtn);
            editBtn.addActionListener(e ->
                    new EditProfileDialog(this, dao, user, this::refresh).setVisible(true)
            );

            rowPanel.add(profileBtn, BorderLayout.CENTER);
            rowPanel.add(editBtn, BorderLayout.EAST);
            userListPanel.add(rowPanel);
        }

        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void styleMainButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(34, 102, 102));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleProfileButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setBackground(new Color(255, 255, 255, 220));
        btn.setForeground(new Color(30, 30, 30));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 102, 102), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    private void styleEditButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(60, 35));
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SplashScreen::new);
    }
}
