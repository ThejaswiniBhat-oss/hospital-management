package com.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * CRUD screen for the PATIENTS table.
 *
 * Columns: name, age, address, disease, allergies, insurance,
 *          doctor_incharge, department
 */
public class PatientPanel extends JPanel {

    private final JTextField tfId        = new JTextField(6);
    private final JTextField tfName      = new JTextField(16);
    private final JTextField tfAge       = new JTextField(6);
    private final JTextField tfAddress   = new JTextField(22);
    private final JTextField tfDisease   = new JTextField(16);
    private final JTextField tfAllergies = new JTextField(16);
    private final JComboBox<String> cbInsurance =
            new JComboBox<>(new String[]{"Yes", "No"});
    private final JTextField tfDoctor     = new JTextField(16);
    private final JTextField tfDepartment = new JTextField(16);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Age", "Address", "Disease",
                         "Allergies", "Insurance", "Doctor In-charge", "Department"}, 0);
    private final JTable table = new JTable(model);

    public PatientPanel(HospitalApp app) {
        setLayout(new BorderLayout(10, 10));

        // ---------- Title bar with Back button ----------
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("< Back");
        back.addActionListener(e -> app.showCard("HOME"));
        JLabel heading = new JLabel("   Patient Records");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        top.add(back);
        top.add(heading);
        add(top, BorderLayout.NORTH);

        // ---------- Form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        addField(form, g, 0, 0, "Patient ID (for update/delete):", tfId);
        addField(form, g, 0, 1, "Name:",            tfName);
        addField(form, g, 2, 1, "Age:",             tfAge);
        addField(form, g, 0, 2, "Address:",         tfAddress);
        addField(form, g, 2, 2, "Disease:",         tfDisease);
        addField(form, g, 0, 3, "Allergies:",       tfAllergies);
        addField(form, g, 2, 3, "Insurance:",       cbInsurance);
        addField(form, g, 0, 4, "Doctor In-charge:", tfDoctor);
        addField(form, g, 2, 4, "Department:",      tfDepartment);

        // ---------- Buttons ----------
        JPanel buttons = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn   = new JButton("View All");
        JButton clearBtn  = new JButton("Clear Form");

        insertBtn.addActionListener(e -> insertPatient());
        updateBtn.addActionListener(e -> updatePatient());
        deleteBtn.addActionListener(e -> deletePatient());
        viewBtn.addActionListener(e   -> viewPatients());
        clearBtn.addActionListener(e  -> clearForm());

        buttons.add(insertBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(viewBtn);
        buttons.add(clearBtn);

        JPanel centre = new JPanel(new BorderLayout());
        centre.add(form, BorderLayout.CENTER);
        centre.add(buttons, BorderLayout.SOUTH);
        add(centre, BorderLayout.CENTER);

        // ---------- Table ----------
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(950, 250));
        add(scroll, BorderLayout.SOUTH);

        // Clicking a row copies it into the form (convenient for update/delete)
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        viewPatients();   // load data as soon as the screen opens
    }

    /** Small helper so the form code stays readable. */
    private void addField(JPanel panel, GridBagConstraints g,
                          int col, int row, String label, JComponent field) {
        g.gridx = col;     g.gridy = row; panel.add(new JLabel(label), g);
        g.gridx = col + 1; g.gridy = row; panel.add(field, g);
    }

    // =========================================================
    // CREATE
    // =========================================================
    private void insertPatient() {
        String sql = "INSERT INTO patients " +
                "(name, age, address, disease, allergies, insurance, doctor_incharge, department) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setInt(2, Integer.parseInt(tfAge.getText().trim()));
            ps.setString(3, tfAddress.getText().trim());
            ps.setString(4, tfDisease.getText().trim());
            ps.setString(5, tfAllergies.getText().trim());
            ps.setString(6, (String) cbInsurance.getSelectedItem());
            ps.setString(7, tfDoctor.getText().trim());
            ps.setString(8, tfDepartment.getText().trim());

            int rows = ps.executeUpdate();
            info(rows + " patient record inserted successfully.");
            clearForm();
            viewPatients();

        } catch (NumberFormatException ex) {
            error("Age must be a number.");
        } catch (SQLException ex) {
            error("Insert failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // READ
    // =========================================================
    private void viewPatients() {
        String sql = "SELECT * FROM patients ORDER BY patient_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            model.setRowCount(0);   // clear the table first
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("address"),
                        rs.getString("disease"),
                        rs.getString("allergies"),
                        rs.getString("insurance"),
                        rs.getString("doctor_incharge"),
                        rs.getString("department")
                });
            }
        } catch (SQLException ex) {
            error("Could not load patients: " + ex.getMessage());
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================
    private void updatePatient() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Patient ID of the record you want to update.");
            return;
        }

        String sql = "UPDATE patients SET name=?, age=?, address=?, disease=?, " +
                "allergies=?, insurance=?, doctor_incharge=?, department=? " +
                "WHERE patient_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setInt(2, Integer.parseInt(tfAge.getText().trim()));
            ps.setString(3, tfAddress.getText().trim());
            ps.setString(4, tfDisease.getText().trim());
            ps.setString(5, tfAllergies.getText().trim());
            ps.setString(6, (String) cbInsurance.getSelectedItem());
            ps.setString(7, tfDoctor.getText().trim());
            ps.setString(8, tfDepartment.getText().trim());
            ps.setInt(9, Integer.parseInt(tfId.getText().trim()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                error("No patient found with that ID.");
            } else {
                info("Patient record updated successfully.");
                viewPatients();
            }
        } catch (NumberFormatException ex) {
            error("ID and Age must be numbers.");
        } catch (SQLException ex) {
            error("Update failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // DELETE
    // =========================================================
    private void deletePatient() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Patient ID of the record you want to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete patient with ID " + tfId.getText().trim() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM patients WHERE patient_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(tfId.getText().trim()));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                error("No patient found with that ID.");
            } else {
                info("Patient record deleted successfully.");
                clearForm();
                viewPatients();
            }
        } catch (NumberFormatException ex) {
            error("Patient ID must be a number.");
        } catch (SQLException ex) {
            error("Delete failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // Helpers
    // =========================================================
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        tfId.setText(String.valueOf(model.getValueAt(row, 0)));
        tfName.setText(String.valueOf(model.getValueAt(row, 1)));
        tfAge.setText(String.valueOf(model.getValueAt(row, 2)));
        tfAddress.setText(String.valueOf(model.getValueAt(row, 3)));
        tfDisease.setText(String.valueOf(model.getValueAt(row, 4)));
        tfAllergies.setText(String.valueOf(model.getValueAt(row, 5)));
        cbInsurance.setSelectedItem(String.valueOf(model.getValueAt(row, 6)));
        tfDoctor.setText(String.valueOf(model.getValueAt(row, 7)));
        tfDepartment.setText(String.valueOf(model.getValueAt(row, 8)));
    }

    private void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfAge.setText("");
        tfAddress.setText("");
        tfDisease.setText("");
        tfAllergies.setText("");
        cbInsurance.setSelectedIndex(0);
        tfDoctor.setText("");
        tfDepartment.setText("");
        table.clearSelection();
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
