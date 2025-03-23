package gui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    // Property change constants
    public static final String PROP_SERVER_ADDED = "serverAdded";
    public static final String PROP_SERVER_REMOVED = "serverRemoved";
    public static final String PROP_SELECTED_SERVER = "selectedServer";
    public static final String PROP_SERVER_STATUS_CHANGED = "serverStatusChanged";

    private Map<Id, Server> servers;
    private Map<Id, ScheduledFuture<?>> healthCheckTasks;
    private Server selectedServer;
    private ScheduledExecutorService healthCheckExecutor;
    private HttpClient httpClient;
    private PropertyChangeSupport pcs;
    private boolean batchOperation = false;

    public ServerManager() {
        servers = new HashMap<>();
        healthCheckTasks = new HashMap<>();
        pcs = new PropertyChangeSupport(this);
        healthCheckExecutor = Executors.newScheduledThreadPool(1);

        // Create a shared HttpClient instance for all health checks
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .executor(Executors.newCachedThreadPool())
                .build();
    }

    public void addServer(Server server) {
        servers.put(server.getId(), server);

        // Listen to server status changes
        server.addPropertyChangeListener(Server.PROP_STATUS, evt -> {
            pcs.firePropertyChange(PROP_SERVER_STATUS_CHANGED, null, server);
        });

        // Start health check for this specific server
        startHealthCheckForServer(server);

        pcs.firePropertyChange(PROP_SERVER_ADDED, null, server);
    }

    public void removeServer(Id id) {
        Server server = servers.get(id);
        if (server != null) {
            // Cancel health check for this server
            stopHealthCheckForServer(id);

            servers.remove(id);
            pcs.firePropertyChange(PROP_SERVER_REMOVED, server, null);

            if (selectedServer != null && selectedServer.getId().equals(id)) {
                setSelectedServer(null);
            }
        }
    }

    public Server getServer(Id id) {
        return servers.get(id);
    }

    public List<Server> getAllServers() {
        return new ArrayList<>(servers.values());
    }

    public void setSelectedServer(Server server) {
        Server oldServer = this.selectedServer;
        this.selectedServer = server;
        pcs.firePropertyChange(PROP_SELECTED_SERVER, oldServer, server);
    }

    public Server getSelectedServer() {
        return selectedServer;
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

    private void startHealthCheckForServer(Server server) {
        Runnable healthCheck = () -> checkServerHealth(server);

        // Schedule health check every 10 seconds for this server
        ScheduledFuture<?> future = healthCheckExecutor.scheduleAtFixedRate(
                healthCheck, 0, 10, TimeUnit.SECONDS);

        // Store the scheduled task for later cancellation if needed
        healthCheckTasks.put(server.getId(), future);
    }

    private void stopHealthCheckForServer(Id serverId) {
        ScheduledFuture<?> task = healthCheckTasks.get(serverId);
        if (task != null) {
            task.cancel(false); // don't interrupt if running
            healthCheckTasks.remove(serverId);
        }
    }

    private void checkServerHealth(Server server) {
        // Create URL for the server health check
        String url = String.format("http://%s:%d/health",
                server.getConfig().getHost(),
                server.getConfig().getPort());

        // Create an HTTP request with a timeout
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        // Send the request asynchronously
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> response.statusCode() >= 200 && response.statusCode() < 300)
                .exceptionally(ex -> false)
                .thenAccept(isOnline -> {
                    // Update server status on the EDT or other thread that can update UI
                    if (isOnline) {
                        server.setStatus(Server.Status.ONLINE);
                    } else {
                        server.setStatus(Server.Status.OFFLINE);
                    }
                });
    }

    public void shutdown() {
        // Cancel all health check tasks
        for (ScheduledFuture<?> task : healthCheckTasks.values()) {
            task.cancel(false);
        }
        healthCheckTasks.clear();

        healthCheckExecutor.shutdown();
        try {
            if (!healthCheckExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                healthCheckExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            healthCheckExecutor.shutdownNow();
        }
    }

    /**
     * Starts a batch operation. During batch operations, property change events
     * are still fired, but can be identified as part of a batch.
     */
    public void startBatchOperation() {
        batchOperation = true;
    }

    /**
     * Returns whether a batch operation is in progress.
     */
    public boolean isBatchOperation() {
        return batchOperation;
    }

    /**
     * Ends a batch operation and optionally fires a special event to notify that a batch has completed.
     * @param fireSaveEvent whether to fire the batchOperationEnd event (which triggers a save)
     */
    public void endBatchOperation(boolean fireSaveEvent) {
        batchOperation = false;
        // Fire a special event to signal the end of a batch operation if requested
        if (fireSaveEvent) {
            pcs.firePropertyChange("batchOperationEnd", null, null);
        }
    }

    /**
     * Ends a batch operation and fires a special event to notify that a batch has completed.
     * For backward compatibility.
     */
    public void endBatchOperation() {
        endBatchOperation(true);
    }

}