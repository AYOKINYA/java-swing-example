package gui.controller;


import gui.model.Id;
import gui.model.Server;
import gui.model.Config;
import gui.model.ServerManager;
import gui.view.ContextMenuView;
import gui.view.ServerDialogView;
import gui.view.SideBarView;
import gui.view.ContentView;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class ContextMenuController {
    private final ContextMenuView contextMenuView;
    private final ServerManager serverManager;
    private SideBarView sideBarView;
    private ContentView contentView;

    public ContextMenuController(ContextMenuView contextMenuView, ServerManager serverManager) {
        this.contextMenuView = contextMenuView;
        this.serverManager = serverManager;

        // Register action listeners
        registerListeners();
    }

    public void attachToSideBarView(SideBarView sideBarView) {
        this.sideBarView = sideBarView;

        sideBarView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenuView.showForSideBar(sideBarView, e);
                }
            }
        });
    }

    public void attachToContentView(ContentView contentView) {
        this.contentView = contentView;

        contentView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenuView.showForContentView(contentView, e);
                }
            }
        });
    }

    private void registerListeners() {
        // Add Server action
        contextMenuView.getAddServerItem().addActionListener(e -> showServerDialog(false)); // false for add mode

        // Edit Server action
        contextMenuView.getEditServerItem().addActionListener(e -> showServerDialog(true)); // true for edit mode

        // Delete Server action
        contextMenuView.getDeleteServerItem().addActionListener(e -> deleteSelectedServer());
    }

    private void showServerDialog(boolean isEditMode) {
        String title = isEditMode ? "Edit Server" : "Add New Server";
        // Get the parent component
        JComponent parent = (JComponent)contextMenuView.getInvoker();
        ServerDialogView dialogView = new ServerDialogView((JFrame) SwingUtilities.getWindowAncestor(parent), title, isEditMode);

        Server serverToEdit = null;
        if (isEditMode) {
            serverToEdit = getSelectedServer();
            if (serverToEdit == null) return;
            dialogView.setServerData(
                    serverToEdit.getConfig().getName(),
                    serverToEdit.getConfig().getHost(),
                    serverToEdit.getConfig().getPort()
            );
        }

        Server finalServerToEdit = serverToEdit;
        dialogView.getOkButton().addActionListener(e -> {
            String name = dialogView.getNameFieldText();
            String host = dialogView.getHostFieldText();
            String portText = dialogView.getPortFieldText();

            if (name.isEmpty() || host.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialogView,
                        "Server name and host cannot be empty",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int port = Integer.parseInt(portText);
                if (!isEditMode) {
                    // Add new server
                    Id id = new Id(UUID.randomUUID().toString());
                    Config config = new Config(name, host, port);
                    Server server = new Server(id, config);
                    serverManager.addServer(server);
                } else {
                    // Edit existing server
                    serverManager.startBatchOperation();
                    try {
                        Config newConfig = new Config(name, host, port);
                        Id serverId = finalServerToEdit.getId();
                        serverManager.removeServer(serverId);
                        Server updatedServer = new Server(serverId, newConfig);
                        serverManager.addServer(updatedServer);
                    } finally {
                        serverManager.endBatchOperation();
                    }
                }
                dialogView.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialogView,
                        "Port must be a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        dialogView.getCancelButton().addActionListener(e -> dialogView.dispose());

        dialogView.setVisible(true);
    }

    private void deleteSelectedServer() {
        // Get the parent component
        JComponent parent = (JComponent)contextMenuView.getInvoker();

        // Get the selected server - either from sidebar or content view
        Server server = getSelectedServer();
        if (server == null) return;

        int result = JOptionPane.showConfirmDialog(
                parent,
                "Are you sure you want to delete the server: " + server.getConfig().getName() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            serverManager.removeServer(server.getId());
        }
    }

    private Server getSelectedServer() {
        // If triggered from the sidebar
        if (contextMenuView.getInvoker() == sideBarView) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) sideBarView.getLastSelectedPathComponent();
            if (node != null && !node.isRoot() && node.getUserObject() instanceof Server) {
                return (Server) node.getUserObject();
            }
        }
        // If triggered from the content view
        else if (contextMenuView.getInvoker() == contentView) {
            return serverManager.getSelectedServer();
        }

        return null;
    }
}