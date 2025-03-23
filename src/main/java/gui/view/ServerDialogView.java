package gui.view;

import javax.swing.*;
import java.awt.*;

public class ServerDialogView extends JDialog {
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton okButton;
    private JButton cancelButton;
    private boolean isEditMode; // Flag to indicate if it's edit mode

    public ServerDialogView(JFrame parent, String title, boolean isEditMode) {
        super(parent, title, true); // Modal dialog
        this.isEditMode = isEditMode;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(parent); // Center on parent

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        nameField = new JTextField(20);
        hostField = new JTextField("localhost", 20);
        portField = new JTextField("8080", 5);

        inputPanel.add(new JLabel("Server Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Host:"));
        inputPanel.add(hostField);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(portField);

        JPanel buttonPanel = new JPanel();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to set initial values for edit mode
    public void setServerData(String name, String host, int port) {
        nameField.setText(name);
        hostField.setText(host);
        portField.setText(String.valueOf(port));
    }

    public String getNameFieldText() {
        return nameField.getText().trim();
    }

    public String getHostFieldText() {
        return hostField.getText().trim();
    }

    public String getPortFieldText() {
        return portField.getText().trim();
    }

    public JButton getOkButton() {
        return okButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public boolean isEditMode() {
        return isEditMode;
    }
}