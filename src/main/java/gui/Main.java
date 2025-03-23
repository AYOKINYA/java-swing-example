package gui;

import com.formdev.flatlaf.FlatLightLaf;
import gui.controller.*;
import gui.model.*;
import gui.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


public class Main extends JFrame {
    // Model
    private ServerManager serverManager;
    private ConfigFileManager configFileManager;
    private static final String CONFIG_FILE_PATH = "config.yaml";
    // Views
    private TopBarView topBarView;
    private SideBarView sideBarView;
    private ContentView contentView;
    private BottomBarView bottomBarView;
    private ContextMenuView contextMenuView;

    // Controllers
    private TopBarController topBarController;
    private SideBarController sideBarController;
    private ContentController contentController;
    private BottomBarController bottomBarController;
    private ContextMenuController contextMenuController;

    public Main() {
        setTitle("Server Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize model
        configFileManager = new ConfigFileManager();
        serverManager = new ServerManager();

        // Setup server change listener to save changes to config file
        setupServerChangeListener();

        // Initialize views
        topBarView = new TopBarView();
        sideBarView = new SideBarView();
        contentView = new ContentView();
        bottomBarView = new BottomBarView();
        contextMenuView = new ContextMenuView();

        // Initialize controllers
        topBarController = new TopBarController(topBarView, serverManager);
        sideBarController = new SideBarController(sideBarView, serverManager);
        contentController = new ContentController(contentView, serverManager);
        bottomBarController = new BottomBarController(bottomBarView, serverManager);
        contextMenuController = new ContextMenuController(contextMenuView, serverManager);

        // Attach to sidebar and content view
        contextMenuController.attachToSideBarView(sideBarView);
        contextMenuController.attachToContentView(contentView);

        // Add components to frame
        add(topBarView, BorderLayout.NORTH);
        add(new JScrollPane(sideBarView), BorderLayout.WEST);
        add(contentView, BorderLayout.CENTER);
        add(bottomBarView, BorderLayout.SOUTH);

        // Load servers from configuration file or add sample data if file doesn't exist
        loadConfigurationsFromFile();

        // Add window listener to clean up resources
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanupAndExit();
            }
        });
    }

    private void setupServerChangeListener() {
        // Listen for individual server changes when not in a batch operation
        serverManager.addPropertyChangeListener(ServerManager.PROP_SERVER_ADDED, evt -> {
            if (!serverManager.isBatchOperation()) {
                    saveConfigurationsToFile();
                }
        });
        serverManager.addPropertyChangeListener(ServerManager.PROP_SERVER_REMOVED, evt -> {
            if (!serverManager.isBatchOperation()) {
        saveConfigurationsToFile();
    }
        });

        // Listen for the end of batch operations
        serverManager.addPropertyChangeListener("batchOperationEnd", evt -> {
            saveConfigurationsToFile();
        });
    }

    private void saveConfigurationsToFile() {
        // Save configurations to file
        try {
            configFileManager.saveConfigurations(serverManager.getAllServers(), CONFIG_FILE_PATH);
        } catch (Exception e) {
            // Handle save errors
            System.err.println("Failed to save configurations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // src/main/java/gui/Main.java
    private void loadConfigurationsFromFile() {
        List<Server> servers = configFileManager.loadConfigurations(CONFIG_FILE_PATH);

        // Start batch operation
        serverManager.startBatchOperation();
        try {
            if (servers.isEmpty()) {
                addSampleServers();
                saveConfigurationsToFile();
            } else {
                // Add loaded servers to the server manager
                for (Server server : servers) {
                    serverManager.addServer(server);
                }
            }
        } finally {
            // Always end batch operation, but with a flag to prevent auto-save
            serverManager.endBatchOperation(false);
        }
    }

    private void addSampleServers() {
        // Add some sample servers
        Id id1 = new Id("srv-001");
        Config config1 = new Config("Web Server", "127.0.0.1", 4000);
        Server server1 = new Server(id1, config1);

        Id id2 = new Id("db-001");
        Config config2 = new Config("Database Server", "192.168.1.101", 5432);
        Server server2 = new Server(id2, config2);

        Id id3 = new Id("app-001");
        Config config3 = new Config("Application Server", "192.168.1.102", 8080);
        Server server3 = new Server(id3, config3);

        serverManager.addServer(server1);
        serverManager.addServer(server2);
        serverManager.addServer(server3);
    }

    private void cleanupAndExit() {
        // Just perform the existing cleanup
        bottomBarView.cleanup();
        serverManager.shutdown();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set the look and feel to the system look and feel
                FlatLightLaf.setup();
                UIManager.put("Tree.showDefaultIcons", true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Main app = new Main();
            app.setVisible(true);
        });
    }
}
