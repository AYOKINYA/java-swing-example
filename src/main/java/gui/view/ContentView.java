// src/main/java/gui/view/ContentView.java
package gui.view;

import gui.model.Server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ContentView extends JPanel {
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JTable detailsTable;
    private DefaultTableModel tableModel;
    private Server currentServer; // Track current server for context menu

    // Buttons
    private JButton saveButton;
    private JButton undoButton;

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
        String[] columns = {"Property", "Value", "Description", "Extra"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells editable
                return true;
            }
        };
        detailsTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(detailsTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));

        // Disable editing
        nameField.setEditable(false);
        hostField.setEditable(false);
        portField.setEditable(false);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save Configurations");
        undoButton = new JButton("Undo");
        undoButton.setEnabled(false); // Initially disabled
        buttonPanel.add(undoButton);
        buttonPanel.add(saveButton);

        // Add action listeners to buttons
        // You can add action listener to save button here.
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save button clicked");
            }
        });

        // Add everything to content panel
        contentPanel.add(statusPanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(tableScrollPane, BorderLayout.SOUTH);
        contentPanel.add(buttonPanel, BorderLayout.EAST);

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
        // Adjust column width
        adjustAllColumnWidth();

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
                {"ID", server.getId().getId(), "Server unique identifier", "Extra Info 1"},
                {"Status", server.getStatus(), "Current server status", "Extra Info 2"},
                {"Full Address", server.getConfig().getHost() + ":" + server.getConfig().getPort(), "Server network address", "Extra Info 3"},
                {"Description", "This is a sample description", "Description of the server", "Extra Info 4"}
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


    private void adjustColumnWidth(int columnIndex) {
        TableColumn column = detailsTable.getColumnModel().getColumn(columnIndex);
        int width = 0;

        // Get width of header
        TableCellRenderer renderer = detailsTable.getTableHeader().getDefaultRenderer();
        Component headerComp = renderer.getTableCellRendererComponent(detailsTable, column.getHeaderValue(), false, false, 0, 0);
        width = Math.max(width, headerComp.getPreferredSize().width);

        // Get width of each cell
        for (int row = 0; row < detailsTable.getRowCount(); row++) {
            TableCellRenderer cellRenderer = detailsTable.getCellRenderer(row, columnIndex);
            Component cellComp = detailsTable.prepareRenderer(cellRenderer, row, columnIndex);
            width = Math.max(width, cellComp.getPreferredSize().width);
        }

        // Add some padding
        width += 3;

        // Set the preferred width
        column.setPreferredWidth(width);
    }

    private void adjustAllColumnWidth() {
        TableColumnModel columnModel = detailsTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            adjustColumnWidth(i);
        }
    }
}