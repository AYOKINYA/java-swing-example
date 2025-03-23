package gui.model;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.util.*;

public class ConfigFileManager {

    public void saveConfigurations(List<Server> servers, String filePath) {
        try {
            // Configure YAML options
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);

            // Convert servers to serializable map
            List<Map<String, Object>> serversData = new ArrayList<>();
            for (Server server : servers) {
                Map<String, Object> serverMap = new HashMap<>();

                // Store Config
                Map<String, Object> configMap = new HashMap<>();
                configMap.put("name", server.getConfig().getName());
                configMap.put("host", server.getConfig().getHost());
                configMap.put("port", server.getConfig().getPort());
                serverMap.put("server", configMap);

                serversData.add(serverMap);
            }

            // Create parent directories if they don't exist
            File file = new File(filePath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            // Write to YAML file
            try (FileWriter writer = new FileWriter(filePath)) {
                yaml.dump(serversData, writer);
                System.out.println("Successfully saved " + servers.size() + " server configurations to " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Failed to save configurations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Server> loadConfigurations(String filePath) {
        List<Server> servers = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Configuration file not found: " + filePath);
            return servers;
        }

        try {
            Yaml yaml = new Yaml();
            try (InputStream inputStream = new FileInputStream(file)) {
                List<Map<String, Object>> serversData = yaml.load(inputStream);

                if (serversData != null) {
                    int idx = 0;
                    for (Map<String, Object> serverMap : serversData) {
                        // Extract ID
                        Id id = new Id("srv-" + idx);

                        // Extract Config
                        Map<String, Object> configMap = (Map<String, Object>) serverMap.get("server");
                        String name = (String) configMap.get("name");
                        String host = (String) configMap.get("host");
                        int port = ((Number) configMap.get("port")).intValue(); // Handle any Number type

                        Config config = new Config(name, host, port);
                        Server server = new Server(id, config);
                        servers.add(server);
                        ++idx;
                    }
                }

                System.out.println("Successfully loaded " + servers.size() + " server configurations from " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Failed to load configurations: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassCastException | NullPointerException e) {
            System.err.println("Malformed configuration file: " + e.getMessage());
            e.printStackTrace();
        }

        return servers;
    }
}