package statsVisualiser.gui;

import DAO.UserProfileDao;
import Models.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CreateProfileDialog extends JDialog {
    private final JTextField nameField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JComboBox<String> sexBox = new JComboBox<>(new String[]{"male", "female"});
    private final JTextField dobField = new JTextField("2000-01-01", 15);
    private final JTextField heightField = new JTextField(15);
    private final JTextField weightField = new JTextField(15);
    private final JComboBox<String> unitsBox = new JComboBox<>(new String[]{"metric", "imperial"});

    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Insets FIELD_INSETS = new Insets(4, 8, 4, 8);

    public CreateProfileDialog(JFrame owner, UserProfileDao dao, Runnable onSuccess) {
        super(owner, "Create Profile", true);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = FIELD_INSETS;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addRow(formPanel, gbc, "Name", nameField);
        addRow(formPanel, gbc, "Email", emailField);
        addRow(formPanel, gbc, "Sex", sexBox);
        addRow(formPanel, gbc, "DOB (yyyy-MM-dd)", dobField);
        addRow(formPanel, gbc, "Height", heightField);
        addRow(formPanel, gbc, "Weight", weightField);
        addRow(formPanel, gbc, "Units", unitsBox);

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(DEFAULT_FONT);
        saveBtn.setPreferredSize(new Dimension(100, 30));
        saveBtn.addActionListener(e -> {
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

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        field.setFont(DEFAULT_FONT);

        gbc.gridx = 0;
        gbc.weightx = 0.2;
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private UserProfile buildProfile() throws ParseException {
        return new UserProfile(
            nameField.getText().trim(),
            emailField.getText().trim(),
            sexBox.getSelectedItem().toString(),
            new SimpleDateFormat("yyyy-MM-dd").parse(dobField.getText().trim()),
            Float.parseFloat(heightField.getText().trim()),
            Float.parseFloat(weightField.getText().trim()),
            unitsBox.getSelectedItem().toString()
        );
    }

    private void error(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserProfileDao dao = new UserProfileDao();
            JFrame dummyFrame = new JFrame();
            Runnable onSuccess = () -> System.out.println("Profile created successfully!");
            CreateProfileDialog dialog = new CreateProfileDialog(dummyFrame, dao, onSuccess);
            dialog.setVisible(true);
        });
    }
}
