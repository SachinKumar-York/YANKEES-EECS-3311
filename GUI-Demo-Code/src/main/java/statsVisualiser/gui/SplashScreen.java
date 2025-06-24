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
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        setTitle("User Profiles");
        setSize(400, 500);
        setLayout(new BorderLayout());

  
        JButton createBtn = new JButton("Create Profile");
        createBtn.setPreferredSize(new Dimension(150, 40));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(createBtn);

        createBtn.addActionListener(
            e -> new CreateProfileDialog(this, dao, this::refresh).setVisible(true)
        );

        add(new JScrollPane(userListPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refresh();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void refresh() {
        userListPanel.removeAll();

        List<UserProfile> users = dao.getAllUserProfiles();
        for (UserProfile user : users) {
            JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
            rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            
            JButton profileBtn = new JButton(user.getName());
            profileBtn.setFocusPainted(false);
            profileBtn.setContentAreaFilled(true);
            profileBtn.setHorizontalAlignment(SwingConstants.LEFT);

            profileBtn.addActionListener(e -> {
                Session.login(user.getUserId());               
                new Dashboard(this, user).setVisible(true);
            });
            JButton editBtn = new JButton("Edit");
            editBtn.setPreferredSize(new Dimension(70, 30));
            editBtn.addActionListener(e ->
                new EditProfileDialog(this, dao, user, this::refresh).setVisible(true)
            );

            rowPanel.add(profileBtn, BorderLayout.CENTER);
            rowPanel.add(editBtn, BorderLayout.EAST);

            userListPanel.add(rowPanel);
            userListPanel.add(Box.createVerticalStrut(5)); // spacing between profiles
        }

        userListPanel.revalidate();
        userListPanel.repaint();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(SplashScreen::new);
    }
}
