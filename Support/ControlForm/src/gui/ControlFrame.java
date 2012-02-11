package gui;

import commands.ICommandProcessor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 20.09.11
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class ControlFrame extends JFrame {
    private ControlPanel panel;
    public ControlFrame() {
    //Create and set up the window.
        super();
        panel = new ControlPanel();

        setName("Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setOpaque(true); //content panes must be opaque
        setContentPane(panel);

        //frame.pack();
        setVisible(true);
        setAlwaysOnTop(true);
        setSize(330, 120);
    }
}
