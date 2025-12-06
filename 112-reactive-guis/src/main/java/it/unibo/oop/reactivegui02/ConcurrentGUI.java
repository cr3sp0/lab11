package it.unibo.oop.reactivegui02;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel();
        panel.add(display);
        final JButton upButton = new JButton("count");
        panel.add(upButton);
        final JButton downButton = new JButton("countdown");
        panel.add(downButton);
        final JButton exit = new JButton("stop");
        panel.add(exit);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        upButton.addActionListener(e -> {
            if (!agent.getCountingStatus()) {
                agent.changeCounting();
            }
        });

        downButton.addActionListener(e -> {
            if (agent.getCountingStatus()) {
                agent.changeCounting();
            }
        });

        exit.addActionListener(e -> {
            agent.stopCounting();
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        });
    }

    private final class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean upDown = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if (upDown) {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                        this.counter++;
                    } else {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                        this.counter--;
                    }

                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    System.out.println("exeption occured: " + ex);
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void changeCounting() {
            this.upDown = !this.upDown;
        }

        public boolean getCountingStatus() {
            return this.upDown;
        }

    }
}
