package gui.controller;

import gui.model.Config;
import gui.model.Id;
import gui.model.Server;
import gui.model.ServerManager;
import gui.view.TopBarView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TopBarController implements PropertyChangeListener {
    private TopBarView view;
    private ServerManager model;

    public TopBarController(TopBarView view, ServerManager model) {
        this.view = view;
        this.model = model;

        // Register for model events
        model.addPropertyChangeListener(this);

        // Add button listeners
        view.setAddButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddServerDialog();
            }
        });

        view.setRemoveButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Server selectedServer = model.getSelectedServer();
                if (selectedServer != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(view),
                        "Are you sure you want to remove server '" + selectedServer.getConfig().getName() + "'?",
                        "Confirm Remove",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        model.removeServer(selectedServer.getId());
                    }
                }
            }
        });
    }

    private void showAddServerDialog() {
        JPanel panel = view.createAddServerPanel();

        int result = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(view),
            panel,
            "Add New Server",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            JTextField nameField = (JTextField) panel.getComponent(1);
            JTextField hostField = (JTextField) panel.getComponent(3);
            JTextField portField = (JTextField) panel.getComponent(5);

            String name = nameField.getText().trim();
            String host = hostField.getText().trim();
            String portText = portField.getText().trim();

            if (name.isEmpty() || host.isEmpty() || portText.isEmpty()) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(view),
                    "All fields are required",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                int port = Integer.parseInt(portText);
                Id id = new Id(java.util.UUID.randomUUID().toString());
                Config config = new Config(name, host, port);
                Server server = new Server(id, config);
                model.addServer(server);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(view),
                    "Port must be a valid number",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case ServerManager.PROP_SELECTED_SERVER:
                Server server = (Server) evt.getNewValue();
                if (server != null) {
                    view.updateTitle("Server: " + server.getConfig().getName());
                } else {
                    view.updateTitle("Server Management");
                }
                view.setRemoveButtonEnabled(server != null);
                break;
            case ServerManager.PROP_SERVER_STATUS_CHANGED:
                Server changedServer = (Server) evt.getNewValue();
                Server selectedServer = model.getSelectedServer();
                if (selectedServer != null && selectedServer.equals(changedServer)) {
//                    view.updateStatus("Server " + changedServer.getConfig().getName() +
//                                     " is now " + changedServer.getStatus());
                }
                break;
        }
    }

    // Add this method to your TopBarController
    private void updateTitleBasedOnStatus(Server.Status status) {
        // Depending on your TopBarView implementation, you could:
        // Change title colors, add icons, or modify other UI elements based on status
        switch (status) {
            case ONLINE:
                view.setTitleColor(Color.GREEN);
                break;
            case OFFLINE:
                view.setTitleColor(Color.RED);
                break;
            case UNKNOWN:
                view.setTitleColor(Color.GRAY);
                break;
        }
    }
}
