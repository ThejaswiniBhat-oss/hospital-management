package com.hospital;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

/**
 * MAIN CLASS.
 *
 * Shows a home screen that asks the user to choose which records they want
 * to manage: Patients, Doctors, or Administration.
 * Uses a CardLayout to switch between the home screen and each module.
 */
public class HospitalApp extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public HospitalApp() {
        setTitle("Hospital Patient Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);   // centre the window on screen

        cards.add(buildHomeScreen(),           "HOME");
        cards.add(new PatientPanel(this),      "PATIENT");
        cards.add(new DoctorPanel(this),       "DOCTOR");
        cards.add(new AdminPanel(this),        "ADMIN");

        add(cards);
        showCard("HOME");
    }

    /** Switches the visible screen. Called by the panels' Back button too. */
    public void showCard(String name) {
        cardLayout.show(cards, name);
    }

    /**
     * The first screen the user sees: three big buttons.
     */
    private JPanel buildHomeScreen() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel title = new JLabel("Hospital Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));

        JLabel subtitle = new JLabel("Select the records you want to manage");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton patientBtn = makeMenuButton("Patients");
        JButton doctorBtn  = makeMenuButton("Doctors");
        JButton adminBtn   = makeMenuButton("Administration");

        patientBtn.addActionListener(e -> showCard("PATIENT"));
        doctorBtn.addActionListener(e  -> showCard("DOCTOR"));
        adminBtn.addActionListener(e   -> showCard("ADMIN"));

        JButton testBtn = new JButton("Test Database Connection");
        testBtn.addActionListener(e -> testConnection());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy = 0; panel.add(title, gbc);
        gbc.gridy = 1; panel.add(subtitle, gbc);
        gbc.gridy = 2; panel.add(patientBtn, gbc);
        gbc.gridy = 3; panel.add(doctorBtn, gbc);
        gbc.gridy = 4; panel.add(adminBtn, gbc);
        gbc.gridy = 5; panel.add(testBtn, gbc);

        return panel;
    }

    private JButton makeMenuButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(280, 55));
        button.setFont(new Font("SansSerif", Font.PLAIN, 18));
        return button;
    }

    /**
     * Handy during the demo: proves the JDBC connection is alive
     * before you even touch a table.
     */
    private void testConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            JOptionPane.showMessageDialog(this,
                    "Connected to MySQL successfully.",
                    "Connection Test", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Connection failed:\n" + ex.getMessage(),
                    "Connection Test", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Swing components must be created on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new HospitalApp().setVisible(true));
    }
}
