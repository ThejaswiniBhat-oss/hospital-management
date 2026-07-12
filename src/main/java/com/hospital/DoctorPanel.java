package com.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * CRUD screen for the DOCTORS table.
 *
 * Columns: name, age, experience_years, department, cases_handled, salary
 */
public class DoctorPanel extends JPanel {

    private final JTextField tfId         = new JTextField(6);
    private final JTextField tfName       = new JTextField(16);
    private final JTextField tfAge        = new JTextField(6);
    private final JTextField tfExperience = new JTextField(6);
    private final JTextField tfDepartment = new JTextField(16);
    private final JTextField tfCases      = new JTextField(8);
    private final JTextField tfSalary     = new JTextField(10);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Age", "Experience (yrs)",
                         "Department", "Cases Handled", "Salary"}, 0);
    private final JTable table = new JTable(model);

    public DoctorPanel(HospitalApp app) {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("< Back");
        back.addActionListener(e -> app.showCard("HOME"));
        JLabel heading = new JLabel("   Doctor Records");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        top.add(back);
        top.add(heading);
        add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Doctor Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        addField(form, g, 0, 0, "Doctor ID (for update/delete):", tfId);
        addField(form, g, 0, 1, "Name:",                tfName);
        addField(form, g, 2, 1, "Age:",                 tfAge);
        addField(form, g, 0, 2, "Years of Experience:", tfExperience);
        addField(form, g, 2, 2, "Department:",          tfDepartment);
        addField(form, g, 0, 3, "No. of Cases Handled:", tfCases);
        addField(form, g, 2, 3, "Salary:",              tfSalary);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn   = new JButton("View All");
        JButton clearBtn  = new JButton("Clear Form");

        insertBtn.addActionListener(e -> insertDoctor());
        updateBtn.addActionListener(e -> updateDoctor());
        deleteBtn.addActionListener(e -> deleteDoctor());
        viewBtn.addActionListener(e   -> viewDoctors());
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(950, 250));
        add(scroll, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        viewDoctors();
    }

    private void addField(JPanel panel, GridBagConstraints g,
                          int col, int row, String label, JComponent field) {
        g.gridx = col;     g.gridy = row; panel.add(new JLabel(label), g);
        g.gridx = col + 1; g.gridy = row; panel.add(field, g);
    }

    // =========================================================
    // CREATE
    // =========================================================
    private void insertDoctor() {
        String sql = "INSERT INTO doctors " +
                "(name, age, experience_years, department, cases_handled, salary) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setInt(2, Integer.parseInt(tfAge.getText().trim()));
            ps.setInt(3, Integer.parseInt(tfExperience.getText().trim()));
            ps.setString(4, tfDepartment.getText().trim());
            ps.setInt(5, Integer.parseInt(tfCases.getText().trim()));
            ps.setDouble(6, Double.parseDouble(tfSalary.getText().trim()));

            int rows = ps.executeUpdate();
            info(rows + " doctor record inserted successfully.");
            clearForm();
            viewDoctors();

        } catch (NumberFormatException ex) {
            error("Age, Experience, Cases and Salary must be numbers.");
        } catch (SQLException ex) {
            error("Insert failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // READ
    // =========================================================
    private void viewDoctors() {
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getInt("experience_years"),
                        rs.getString("department"),
                        rs.getInt("cases_handled"),
                        rs.getDouble("salary")
                });
            }
        } catch (SQLException ex) {
            error("Could not load doctors: " + ex.getMessage());
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================
    private void updateDoctor() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Doctor ID of the record you want to update.");
            return;
        }

        String sql = "UPDATE doctors SET name=?, age=?, experience_years=?, " +
                "department=?, cases_handled=?, salary=? WHERE doctor_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setInt(2, Integer.parseInt(tfAge.getText().trim()));
            ps.setInt(3, Integer.parseInt(tfExperience.getText().trim()));
            ps.setString(4, tfDepartment.getText().trim());
            ps.setInt(5, Integer.parseInt(tfCases.getText().trim()));
            ps.setDouble(6, Double.parseDouble(tfSalary.getText().trim()));
            ps.setInt(7, Integer.parseInt(tfId.getText().trim()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                error("No doctor found with that ID.");
            } else {
                info("Doctor record updated successfully.");
                viewDoctors();
            }
        } catch (NumberFormatException ex) {
            error("ID, Age, Experience, Cases and Salary must be numbers.");
        } catch (SQLException ex) {
            error("Update failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // DELETE
    // =========================================================
    private void deleteDoctor() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Doctor ID of the record you want to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete doctor with ID " + tfId.getText().trim() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM doctors WHERE doctor_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(tfId.getText().trim()));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                error("No doctor found with that ID.");
            } else {
                info("Doctor record deleted successfully.");
                clearForm();
                viewDoctors();
            }
        } catch (NumberFormatException ex) {
            error("Doctor ID must be a number.");
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
        tfExperience.setText(String.valueOf(model.getValueAt(row, 3)));
        tfDepartment.setText(String.valueOf(model.getValueAt(row, 4)));
        tfCases.setText(String.valueOf(model.getValueAt(row, 5)));
        tfSalary.setText(String.valueOf(model.getValueAt(row, 6)));
    }

    private void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfAge.setText("");
        tfExperience.setText("");
        tfDepartment.setText("");
        tfCases.setText("");
        tfSalary.setText("");
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
