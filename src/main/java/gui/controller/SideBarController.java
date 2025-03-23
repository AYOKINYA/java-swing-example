// src/main/java/gui/controller/SideBarController.java
package gui.controller;

import gui.model.Server;
import gui.model.ServerManager;
import gui.view.SideBarView;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SideBarController implements PropertyChangeListener {
    private SideBarView view;
    private ServerManager model;

    public SideBarController(SideBarView view, ServerManager model) {
        this.view = view;
        this.model = model;

        // Initialize view with existing servers
        for (Server server : model.getAllServers()) {
            view.addServerNode(server.getId().getId(), server.getConfig().getName(), server);
        }

        // Register for model events
        model.addPropertyChangeListener(this);

        // Add tree selection listener
        view.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) view.getLastSelectedPathComponent();
                if (node == null || node.isRoot()) return;

                Object userObject = node.getUserObject();
                if (userObject instanceof Server) {
                    model.setSelectedServer((Server) userObject);
                }
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case ServerManager.PROP_SERVER_ADDED:
                Server addedServer = (Server) evt.getNewValue();
                view.addServerNode(
                        addedServer.getId().getId(),
                        addedServer.getConfig().getName(),
                        addedServer
                );
                break;

            case ServerManager.PROP_SERVER_REMOVED:
                Server removedServer = (Server) evt.getOldValue();
                view.removeServerNode(removedServer.getId().getId());
                break;

            case ServerManager.PROP_SERVER_STATUS_CHANGED:
                Server changedServer = (Server) evt.getNewValue();
                view.updateServerNode(changedServer.getId().getId(), changedServer);
                break;
        }
    }
}