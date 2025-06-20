package statsVisualiser.gui;

import DAO.UserProfileDao;
import Models.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateProfileDialog extends JDialog {
    private final JTextField nameField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JTextField sexField = new JTextField(15);
    private final JTextField dobField = new JTextField("2000-01-01", 15);
    private final JTextField heightField = new JTextField(15);
    private final JTextField weightField = new JTextField(15);
    private final JComboBox<String> unitsBox = new JComboBox<>(new String[]{"metric", "imperial"});

    public CreateProfileDialog(JFrame owner, UserProfileDao dao, Runnable onSuccess) {
        super(owner, "Create Profile", true);
        setLayout(new GridLayout(0, 2, 10, 10));

        add(new JLabel("Name"));  add(nameField);
        add(new JLabel("Email")); add(emailField);
        add(new JLabel("Sex"));   add(sexField);
        add(new JLabel("DOB (yyyy-MM-dd)")); add(dobField);
        add(new JLabel("Height ")); add(heightField);
        add(new JLabel("Weight ")); add(weightField);
        add(new JLabel("Units")); add(unitsBox);

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            try {
                UserProfile user = buildProfile();
                int id = dao.insertUserProfile(user);
                if (id > 0) {
                    onSuccess.run();
                    dispose();
                } else {
                    error("Insert failed");
                }
            } catch (Exception ex) {
                error("Invalid input: " + ex.getMessage());
            }
        });

        add(save);
        pack();
        setLocationRelativeTo(owner);
    }

    private UserProfile buildProfile() throws ParseException {
        return new UserProfile(
            nameField.getText().trim(),
            emailField.getText().trim(),
            sexField.getText().trim(),
            new SimpleDateFormat("yyyy-MM-dd").parse(dobField.getText().trim()),
            Float.parseFloat(heightField.getText().trim()),
            Float.parseFloat(weightField.getText().trim()),
            unitsBox.getSelectedItem().toString()
        );
    }

    private void error(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}


