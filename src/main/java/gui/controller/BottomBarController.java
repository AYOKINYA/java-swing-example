package gui.controller;

import gui.model.Server;
import gui.model.ServerManager;
import gui.view.BottomBarView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BottomBarController implements PropertyChangeListener {
    private BottomBarView view;
    private ServerManager model;

    public BottomBarController(BottomBarView view, ServerManager model) {
        this.view = view;
        this.model = model;

        // Register for model events
        model.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case ServerManager.PROP_SELECTED_SERVER:
                Server server = (Server) evt.getNewValue();
                if (server != null) {
                    view.updateStatus("Selected server: " + server.getConfig().getName());
                } else {
                    view.updateStatus("Ready");
                }
                break;

            case ServerManager.PROP_SERVER_ADDED:
                Server addedServer = (Server) evt.getNewValue();
                view.updateStatus("Server added: " + addedServer.getConfig().getName());
                break;

            case ServerManager.PROP_SERVER_REMOVED:
                Server removedServer = (Server) evt.getOldValue();
                view.updateStatus("Server removed: " + removedServer.getConfig().getName());
                break;

            case ServerManager.PROP_SERVER_STATUS_CHANGED:
                Server changedServer = (Server) evt.getNewValue();
                Server selectedServer = model.getSelectedServer();
                if (selectedServer != null && selectedServer.equals(changedServer)) {
                    view.updateStatus("Server " + changedServer.getConfig().getName() +
                                     " is now " + changedServer.getStatus());
                }
                break;
        }
    }
}
