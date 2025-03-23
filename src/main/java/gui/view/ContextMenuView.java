package gui.view;

import javax.swing.*;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

public class ContextMenuView extends JPopupMenu {
    // Menu items as instance variables so they can be accessed by the controller
    private final JMenuItem addServerItem;
    private final JMenuItem editServerItem;
    private final JMenuItem deleteServerItem;


    public ContextMenuView() {
        // Create menu items
        addServerItem = new JMenuItem("Add Server");
        editServerItem = new JMenuItem("Edit Server");
        deleteServerItem = new JMenuItem("Delete Server");

        // Add items to popup menu
        add(addServerItem);
        add(editServerItem);
        addSeparator();
        add(deleteServerItem);
    }

    // Method to show context menu for sidebar
    public void showForSideBar(SideBarView sideBarView, MouseEvent e) {
        // First try the exact location
        TreePath path = sideBarView.getPathForLocation(e.getX(), e.getY());

        // If no path was found, try to find the closest row
        if (path == null) {
            int row = sideBarView.getClosestRowForLocation(e.getX(), e.getY());
            if (row != -1) {
                path = sideBarView.getPathForRow(row);
            }
        }

        if (path != null) {
            sideBarView.setSelectionPath(path);

            // Enable/disable menu items based on selection
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            boolean isRoot = node.isRoot();
            boolean isServerNode = !isRoot && node.getUserObject() != null;

            editServerItem.setEnabled(isServerNode);
            deleteServerItem.setEnabled(isServerNode);
        } else {
            // If clicked outside any node, only enable Add Server
            editServerItem.setEnabled(false);
            deleteServerItem.setEnabled(false);
        }

        // Show context menu
        show(e.getComponent(), e.getX(), e.getY());
    }

    // Method to show context menu for content view
    public void showForContentView(ContentView contentView, MouseEvent e) {
        // For content view, only enable add server by default
        editServerItem.setEnabled(contentView.hasSelectedServer());
        deleteServerItem.setEnabled(contentView.hasSelectedServer());

        // Show context menu
        show(e.getComponent(), e.getX(), e.getY());
    }

    // Getters for the controller to access menu items
    public JMenuItem getAddServerItem() {
        return addServerItem;
    }

    public JMenuItem getEditServerItem() {
        return editServerItem;
    }

    public JMenuItem getDeleteServerItem() {
        return deleteServerItem;
    }
}