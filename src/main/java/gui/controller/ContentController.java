// src/main/java/gui/controller/ContentController.java
package gui.controller;

import gui.model.Server;
import gui.model.ServerManager;
import gui.view.ContentView;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ContentController implements PropertyChangeListener {
    private ContentView view;
    private ServerManager model;

    public ContentController(ContentView view, ServerManager model) {
        this.view = view;
        this.model = model;

        // Register for model events
        model.addPropertyChangeListener(this);
    }

    private void updateViewWithServer(Server server) {
        if (server == null) {
            view.showPlaceholder();
            return;
        }

        view.updateContent(server);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case ServerManager.PROP_SELECTED_SERVER:
                Server server = (Server) evt.getNewValue();
                updateViewWithServer(server);
                break;

            case ServerManager.PROP_SERVER_STATUS_CHANGED:
                Server changedServer = (Server) evt.getNewValue();
                Server selectedServer = model.getSelectedServer();
                if (selectedServer != null && selectedServer.equals(changedServer)) {
                    updateViewWithServer(selectedServer);
                }
                break;
        }
    }
}