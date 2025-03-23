// src/main/java/gui/view/ContentView.java
package gui.view;

import gui.model.Server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ContentView extends JPanel {
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JTable detailsTable;
    private DefaultTableModel tableModel;
    private Server currentServer; // Track current server for context menu

    public ContentView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Create status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Status: No server selected");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(statusLabel);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        nameField = new JTextField(20);
        hostField = new JTextField(20);
        portField = new JTextField(5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Host:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(hostField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(portField, gbc);

        // Create table for additional details
        String[] columns = {"Property", "Value"};
        tableModel = new DefaultTableModel(columns, 0);
        detailsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(detailsTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));

        // Disable editing
        nameField.setEditable(false);
        hostField.setEditable(false);
        portField.setEditable(false);
        detailsTable.setEnabled(false);

        // Add everything to content panel
        contentPanel.add(statusPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Add placeholder message
        showPlaceholder();
    }

    /**
     * Shows a placeholder when no server is selected
     */
    public void showPlaceholder() {
        currentServer = null;
        removeAll();
        JLabel placeholderLabel = new JLabel("Select a server from the sidebar to view details");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        add(placeholderLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Updates the content panel with server details
     * @param name Server name
     * @param host Server host
     * @param port Server port
     * @param status Server status string
     * @param statusColor Color for the status text
     * @param properties Additional properties to display in table
     */
    public void updateContent(String name, String host, int port,
                              String status, Color statusColor,
                              Object[][] properties) {
        removeAll();

        // Update fields
        nameField.setText(name);
        hostField.setText(host);
        portField.setText(String.valueOf(port));

        // Update status
        statusLabel.setText("Status: " + status);
        statusLabel.setForeground(statusColor);

        // Update table
        tableModel.setRowCount(0);
        for (Object[] row : properties) {
            tableModel.addRow(row);
        }

        add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void updateContent(Server server) {
        if (server == null) {
            showPlaceholder();
            return;
        }

        // Store current server for context menu
        currentServer = server;

        String name = server.getConfig().getName();
        String host = server.getConfig().getHost();
        int port = server.getConfig().getPort();

        String status;
        Color statusColor;

        switch (server.getStatus()) {
            case ONLINE:
                status = "ONLINE";
                statusColor = Color.GREEN;
                break;
            case OFFLINE:
                status = "OFFLINE";
                statusColor = Color.RED;
                break;
            default:
                status = "UNKNOWN";
                statusColor = Color.ORANGE;
                break;
        }

        Object[][] properties = {
                {"ID", server.getId().getId()},
                {"Status", server.getStatus()},
                {"Full Address", server.getConfig().getHost() + ":" + server.getConfig().getPort()}
        };

        updateContent(name, host, port, status, statusColor, properties);
    }

    // Helper for context menu
    public boolean hasSelectedServer() {
        return currentServer != null;
    }

    // Getter for context menu
    public Server getCurrentServer() {
        return currentServer;
    }
}