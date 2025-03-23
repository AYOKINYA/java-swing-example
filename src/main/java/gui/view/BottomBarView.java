package gui.view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BottomBarView extends JPanel {
    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer timer;

    public BottomBarView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusLabel = new JLabel("Ready");
        timeLabel = new JLabel();
        updateTimeLabel();

        add(statusLabel, BorderLayout.WEST);
        add(timeLabel, BorderLayout.EAST);

        // Start timer to update time
        timer = new Timer(1000, e -> updateTimeLabel());
        timer.start();
    }

    /**
     * Updates the time label with current time
     */
    private void updateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeLabel.setText(sdf.format(new Date()));
    }

    /**
     * Updates the status message
     * @param message Status message to display
     */
    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Cleans up resources
     */
    public void cleanup() {
        if (timer != null) {
            timer.stop();
        }
    }
}
