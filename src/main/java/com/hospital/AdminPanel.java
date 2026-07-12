package com.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * CRUD screen for the ADMINISTRATORS table (hospital administrative staff).
 *
 * Columns: name, designation, department, shift, contact_number
 */
public class AdminPanel extends JPanel {

    private final JTextField tfId          = new JTextField(6);
    private final JTextField tfName        = new JTextField(16);
    private final JTextField tfDesignation = new JTextField(16);
    private final JTextField tfDepartment  = new JTextField(16);
    private final JComboBox<String> cbShift =
            new JComboBox<>(new String[]{"Morning", "Evening", "Night"});
    private final JTextField tfContact     = new JTextField(14);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Designation", "Department",
                         "Shift", "Contact Number"}, 0);
    private final JTable table = new JTable(model);

    public AdminPanel(HospitalApp app) {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("< Back");
        back.addActionListener(e -> app.showCard("HOME"));
        JLabel heading = new JLabel("   Administration Records");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        top.add(back);
        top.add(heading);
        add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Administrative Staff Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        addField(form, g, 0, 0, "Admin ID (for update/delete):", tfId);
        addField(form, g, 0, 1, "Name:",           tfName);
        addField(form, g, 2, 1, "Designation:",    tfDesignation);
        addField(form, g, 0, 2, "Department:",     tfDepartment);
        addField(form, g, 2, 2, "Shift:",          cbShift);
        addField(form, g, 0, 3, "Contact Number:", tfContact);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn   = new JButton("View All");
        JButton clearBtn  = new JButton("Clear Form");

        insertBtn.addActionListener(e -> insertAdmin());
        updateBtn.addActionListener(e -> updateAdmin());
        deleteBtn.addActionListener(e -> deleteAdmin());
        viewBtn.addActionListener(e   -> viewAdmins());
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

        viewAdmins();
    }

    private void addField(JPanel panel, GridBagConstraints g,
                          int col, int row, String label, JComponent field) {
        g.gridx = col;     g.gridy = row; panel.add(new JLabel(label), g);
        g.gridx = col + 1; g.gridy = row; panel.add(field, g);
    }

    // =========================================================
    // CREATE
    // =========================================================
    private void insertAdmin() {
        String sql = "INSERT INTO administrators " +
                "(name, designation, department, shift, contact_number) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfDesignation.getText().trim());
            ps.setString(3, tfDepartment.getText().trim());
            ps.setString(4, (String) cbShift.getSelectedItem());
            ps.setString(5, tfContact.getText().trim());

            int rows = ps.executeUpdate();
            info(rows + " administrator record inserted successfully.");
            clearForm();
            viewAdmins();

        } catch (SQLException ex) {
            error("Insert failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // READ
    // =========================================================
    private void viewAdmins() {
        String sql = "SELECT * FROM administrators ORDER BY admin_id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("admin_id"),
                        rs.getString("name"),
                        rs.getString("designation"),
                        rs.getString("department"),
                        rs.getString("shift"),
                        rs.getString("contact_number")
                });
            }
        } catch (SQLException ex) {
            error("Could not load administrators: " + ex.getMessage());
        }
    }

    // =========================================================
    // UPDATE
    // =========================================================
    private void updateAdmin() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Admin ID of the record you want to update.");
            return;
        }

        String sql = "UPDATE administrators SET name=?, designation=?, " +
                "department=?, shift=?, contact_number=? WHERE admin_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfDesignation.getText().trim());
            ps.setString(3, tfDepartment.getText().trim());
            ps.setString(4, (String) cbShift.getSelectedItem());
            ps.setString(5, tfContact.getText().trim());
            ps.setInt(6, Integer.parseInt(tfId.getText().trim()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                error("No administrator found with that ID.");
            } else {
                info("Administrator record updated successfully.");
                viewAdmins();
            }
        } catch (NumberFormatException ex) {
            error("Admin ID must be a number.");
        } catch (SQLException ex) {
            error("Update failed: " + ex.getMessage());
        }
    }

    // =========================================================
    // DELETE
    // =========================================================
    private void deleteAdmin() {
        if (tfId.getText().trim().isEmpty()) {
            error("Enter the Admin ID of the record you want to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete administrator with ID " + tfId.getText().trim() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM administrators WHERE admin_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(tfId.getText().trim()));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                error("No administrator found with that ID.");
            } else {
                info("Administrator record deleted successfully.");
                clearForm();
                viewAdmins();
            }
        } catch (NumberFormatException ex) {
            error("Admin ID must be a number.");
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
        tfDesignation.setText(String.valueOf(model.getValueAt(row, 2)));
        tfDepartment.setText(String.valueOf(model.getValueAt(row, 3)));
        cbShift.setSelectedItem(String.valueOf(model.getValueAt(row, 4)));
        tfContact.setText(String.valueOf(model.getValueAt(row, 5)));
    }

    private void clearForm() {
        tfId.setText("");
        tfName.setText("");
        tfDesignation.setText("");
        tfDepartment.setText("");
        cbShift.setSelectedIndex(0);
        tfContact.setText("");
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
