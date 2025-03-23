package gui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Server {
    public enum Status {
        ONLINE, OFFLINE, UNKNOWN
    }

    // Property change constants
    public static final String PROP_STATUS = "status";

    private Id id;
    private Config config;
    private Status status;
    private PropertyChangeSupport pcs;

    public Server(Id id, Config config) {
        this.id = id;
        this.config = config;
        this.status = Status.UNKNOWN;
        this.pcs = new PropertyChangeSupport(this);
    }

    public Id getId() {
        return id;
    }

    public Config getConfig() {
        return config;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Status oldStatus = this.status;
        this.status = status;

        if (oldStatus != status) {
            pcs.firePropertyChange(PROP_STATUS, oldStatus, status);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public String toString() {
        return config.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Server other = (Server) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
