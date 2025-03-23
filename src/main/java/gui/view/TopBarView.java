package gui.view;

import gui.model.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TopBarView extends JPanel {
    private JLabel titleLabel;
    private JButton addButton;
    private JButton removeButton;

    public TopBarView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titleLabel = new JLabel("Server Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add Server");
        removeButton = new JButton("Remove Server");
        removeButton.setEnabled(false);

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        add(titleLabel, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.EAST);
    }

    public void updateTitle(Server server) {
        if (server != null) {
            titleLabel.setText("Server: " + server.getConfig().getName());
        } else {
            titleLabel.setText("Server Management");
        }
    }

    /**
     * Updates the title displayed in the top bar
     * @param title The title to display
     */
    public void updateTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Sets a listener for the add button
     * @param listener The ActionListener to set
     */
    public void setAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    /**
     * Sets a listener for the remove button
     * @param listener The ActionListener to set
     */
    public void setRemoveButtonListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    /**
     * Enables or disables the remove button
     * @param enabled true to enable the button, false to disable
     */
    public void setRemoveButtonEnabled(boolean enabled) {
        removeButton.setEnabled(enabled);
    }

    /**
     * Creates and returns a panel for adding a new server
     * @return JPanel with fields for server data
     */
    public JPanel createAddServerPanel() {
        JTextField nameField = new JTextField(20);
        JTextField hostField = new JTextField(20);
        JTextField portField = new JTextField("8080", 5);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Server Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Host:"));
        panel.add(hostField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);

        return panel;
    }

    public void setTitleColor(Color color) {
        titleLabel.setForeground(color);
    }
}
