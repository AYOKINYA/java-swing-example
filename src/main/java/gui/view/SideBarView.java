package gui.view;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;
import java.util.Map;

public class SideBarView extends JTree {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private Map<String, DefaultMutableTreeNode> serverNodes;

    public SideBarView() {
        rootNode = new DefaultMutableTreeNode("Servers");
        treeModel = new DefaultTreeModel(rootNode);
        serverNodes = new HashMap<>();

        setModel(treeModel);
        expandRow(0);
    }

    /**
     * Adds a server node to the tree
     * @param id Unique identifier as a String
     * @param displayName Display name to show in the tree
     * @param userData User data to associate with the node
     */
    public void addServerNode(String id, String displayName, Object userData) {
        DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(userData);
        serverNodes.put(id, serverNode);

        // Add node to the model safely
        SwingUtilities.invokeLater(() -> {
            treeModel.insertNodeInto(serverNode, rootNode, rootNode.getChildCount());
            // Make sure the new node is visible
            expandPath(new javax.swing.tree.TreePath(rootNode.getPath()));
        });
    }

    /**
     * Removes a server node from the tree
     * @param id Unique identifier of the node to remove
     */
    public void removeServerNode(String id) {
        DefaultMutableTreeNode node = serverNodes.get(id);
        if (node != null) {
            SwingUtilities.invokeLater(() -> {
                treeModel.removeNodeFromParent(node);
                serverNodes.remove(id);
            });
        }
    }

    /**
     * Updates the display of a server node
     * @param id Unique identifier of the node to update
     * @param userData New user data for the node
     */
    public void updateServerNode(String id, Object userData) {
        DefaultMutableTreeNode node = serverNodes.get(id);
        if (node != null) {
            SwingUtilities.invokeLater(() -> {
                node.setUserObject(userData);
                treeModel.nodeChanged(node);
            });
        }
    }

    public void clearServerNodes() {
        SwingUtilities.invokeLater(() -> {
            serverNodes.clear();
            rootNode.removeAllChildren();
            treeModel.reload();
            expandRow(0);
        });
    }
}