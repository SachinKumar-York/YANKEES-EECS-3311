package statsVisualiser.gui;

import DAO.UserProfileDao;
import Models.UserProfile;

import javax.swing.*;
import java.awt.*;

public class EditProfileDialog extends JDialog {
    private final JTextField heightField;
    private final JTextField weightField;
    private final JComboBox<String> unitsBox;

    public EditProfileDialog(JFrame owner, UserProfileDao dao, UserProfile profile, Runnable onSuccess) {
        super(owner, "Edit Profile: " + profile.getName(), true);
        setLayout(new GridLayout(0, 2, 10, 10));

        heightField = new JTextField(String.valueOf(profile.getHeight()), 15);
        weightField = new JTextField(String.valueOf(profile.getWeight()), 15);
        unitsBox = new JComboBox<>(new String[]{"metric", "imperial"});
        unitsBox.setSelectedItem(profile.getUnits());

        add(new JLabel("Height ")); add(heightField);
        add(new JLabel("Weight ")); add(weightField);
        add(new JLabel("Units")); add(unitsBox);

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            try {
                float height = Float.parseFloat(heightField.getText().trim());
                float weight = Float.parseFloat(weightField.getText().trim());
                String units = unitsBox.getSelectedItem().toString();

                boolean updated = dao.updateUserProfile(profile.getUserId(), height, weight, units);
                if (updated) {
                    onSuccess.run();
                    dispose();
                } else {
                    error("Update failed");
                }
            } catch (Exception ex) {
                error("Invalid input: " + ex.getMessage());
            }
        });

        add(save);
        pack();
        setLocationRelativeTo(owner);
    }

    private void error(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Dummy frame to act as owner
            JFrame dummyFrame = new JFrame();
            dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dummyFrame.setSize(300, 200);
            dummyFrame.setVisible(true);

            // Dummy UserProfile with sample data
            UserProfile dummyProfile = new UserProfile(
                    "John Doe",
                    "john@example.com",
                    "Male",
                    new java.util.Date(),
                    1.75f,
                    70f,
                    "metric"
            );
            dummyProfile.setUserId(1);

            // Minimal UserProfileDao with no real DB call
            UserProfileDao dummyDao = new UserProfileDao();

            // Open the EditProfileDialog (no real save behavior needed)
            new EditProfileDialog(dummyFrame, dummyDao, dummyProfile, () -> {}).setVisible(true);
        });
    }

}

