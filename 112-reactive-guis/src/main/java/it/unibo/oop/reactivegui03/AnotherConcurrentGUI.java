package it.unibo.oop.reactivegui03;

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
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    public AnotherConcurrentGUI() {
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
        final TimeAgent timeAgent = new TimeAgent(agent);
        new Thread(agent).start();
        new Thread(timeAgent).start();

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
        private int counter = 0;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if (upDown) {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                        this.counter++;
                    } else {
                        final var nextText = Integer.toString(this.counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
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

    private final class TimeAgent implements Runnable {
        private final int TARGET = 10;

        private double counter;
        final Agent agent;

        public TimeAgent(Agent agent) {
            this.agent = agent;
        }

        @Override
        public void run() {
            while (counter < TARGET) {
                try {
                    Thread.sleep(100);
                    counter += .1;
                } catch (Exception e) {
                    System.out.println("an exception has occured: " + e);
                }
            }
            agent.stopCounting();
        }
    }
}
