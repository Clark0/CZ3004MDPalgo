package mdpalgo.simulator;

import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CountTimer extends JPanel {
    private Timer timer;
    private long startTime = -1;

    private JLabel label;

    public CountTimer() {
        setLayout(new GridBagLayout());

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                long now = System.currentTimeMillis();
                long clockTime = now - startTime;
                label.setText(DurationFormatUtils.formatDuration(clockTime, "mm:ss.SSS"));
            }
        });
        timer.setInitialDelay(0);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!timer.isRunning()) {
                    startTime = -1;
                    timer.start();
                }
            }
        });
        label = new JLabel("...");
        label.setFont(new Font("Serif", Font.PLAIN, 18));
        add(label);
    }

    public void start() {
        if (!timer.isRunning()) {
            startTime = -1;
            timer.start();
        }
    }

    public void stop() {
        startTime = -1;
        timer.stop();
    }
}
