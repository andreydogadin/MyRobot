package commands;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 25.12.11
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class RobotControlButton extends JButton {
    private String command;
    private boolean shouldStop;
    private static final String COMMAND_PREFIX = "Robot";

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isShouldStop() {
        return shouldStop;
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    public RobotControlButton(String label, String command, boolean shouldStop) {
        super(label);
        setCommand(command);
        setEnabled(true);
        setShouldStop(shouldStop);
        addMouseListener(new ButtonsListener());
    }

    public RobotControlButton(String label, boolean shouldStop) {
        super(label);
        setEnabled(true);
        setCommand(COMMAND_PREFIX + label);
        setShouldStop(shouldStop);
        addMouseListener(new ButtonsListener());
    }

    public RobotControlButton(String label, String command) {
        super(label);
        setEnabled(true);
        setCommand(command);
        setShouldStop(true);
        addMouseListener(new ButtonsListener());
    }

    public RobotControlButton(String label) {
        super(label);
        setEnabled(true);
        setCommand(COMMAND_PREFIX + label);
        setShouldStop(true);
        addMouseListener(new ButtonsListener());
    }

}
