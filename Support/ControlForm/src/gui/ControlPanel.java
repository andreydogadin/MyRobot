package gui;

import commands.ButtonsListener;
import commands.RobotControlButton;
import org.eclipse.swt.widgets.Layout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Robot
 * Date: 21.09.11
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */
   public class ControlPanel extends JPanel {

            public ControlPanel() {

                this.setLayout(new GridLayout(5,4));

                add(new DummyButton()); add(new RobotControlButton("Drive")); add(new DummyButton());
                add(new RobotControlButton("On/Off", "RobotChangeOnOff", false));
                add(new RobotControlButton("Left"));add(new DummyButton());add(new RobotControlButton("Right"));
                add(new RobotControlButton("Status", "RobotGetStatus", false));
                add(new DummyButton()); add (new RobotControlButton("Back")); add(new DummyButton());
                add(new RobotControlButton("Start", "RobotStart", false));

                add(new RobotControlButton("X+", "MoveCam X+", false));add(new RobotControlButton("X-", "MoveCam X-", false));
                add(new RobotControlButton("Y+", "MoveCam Y+", false));add(new RobotControlButton("Y-", "MoveCam Y-", false));
                add(new DummyButton());add(new RobotControlButton("X0", "MoveCam X0", false));
                add(new DummyButton());add(new RobotControlButton("Y0", "MoveCam Y0", false));
       }
    }
